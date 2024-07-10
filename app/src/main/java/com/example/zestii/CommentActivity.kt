package com.example.zestii

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue // Add this import
import java.util.Date

class CommentActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var postId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        postId = intent.getStringExtra("postId") ?: ""

        recyclerView = findViewById(R.id.recyclerViewComments)
        recyclerView.layoutManager = LinearLayoutManager(this)
        commentAdapter = CommentAdapter(mutableListOf())
        recyclerView.adapter = commentAdapter

        val commentField = findViewById<EditText>(R.id.commentField)
        val postCommentButton = findViewById<Button>(R.id.postCommentButton)

        postCommentButton.setOnClickListener {
            val commentContent = commentField.text.toString().trim()
            val userId = auth.currentUser?.uid
            val username = auth.currentUser?.displayName ?: "Unknown"

            if (commentContent.isEmpty()) {
                return@setOnClickListener
            }

            if (userId != null) {
                val comment = Comment(
                    userId = userId,
                    username = username,
                    content = commentContent,
                    timestamp = Date()
                )

                db.collection("posts").document(postId).update("comments", FieldValue.arrayUnion(comment))
                commentField.text.clear()
            }
        }

        loadComments()
    }

    private fun loadComments() {
        db.collection("posts").document(postId).get().addOnSuccessListener { document ->
            val post = document.toObject(Post::class.java)
            if (post != null) {
                commentAdapter.updateComments(post.comments)
            }
        }
    }
}

