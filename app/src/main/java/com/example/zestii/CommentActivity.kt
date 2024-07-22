package com.example.zestii

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

        postId = intent.getStringExtra("postId") ?: throw IllegalArgumentException("Post ID is missing")

        recyclerView = findViewById(R.id.recyclerViewComments)
        recyclerView.layoutManager = LinearLayoutManager(this)
        commentAdapter = CommentAdapter(mutableListOf())
        recyclerView.adapter = commentAdapter

        loadComments()

        val commentEditText = findViewById<EditText>(R.id.commentEditText)
        val postCommentButton = findViewById<Button>(R.id.postCommentButton)

        postCommentButton.setOnClickListener {
            val commentContent = commentEditText.text.toString().trim()
            if (commentContent.isNotEmpty()) {
                postComment(commentContent)
                commentEditText.text.clear()
            }
        }
    }

    private fun postComment(content: String) {
        val currentUser = auth.currentUser ?: return
        val userId = currentUser.uid
        val username = currentUser.displayName ?: "Unknown"  // Assuming username is stored in displayName for simplicity

        val comment = Comment(
            commentId = db.collection("comments").document().id,
            postId = postId,
            userId = userId,
            username = username,
            content = content,
            timestamp = Date()
        )

        db.collection("comments").document(comment.commentId).set(comment)
            .addOnSuccessListener {
                loadComments()  // Reload comments after posting
            }
            .addOnFailureListener {
                // Handle failure if needed
            }
    }

    private fun loadComments() {
        db.collection("comments")
            .whereEqualTo("postId", postId)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null || snapshots == null) {
                    return@addSnapshotListener
                }

                val comments = mutableListOf<Comment>()
                for (document in snapshots.documents) {
                    val comment = document.toObject(Comment::class.java)
                    if (comment != null) {
                        comments.add(comment)
                    }
                }
                commentAdapter.updateComments(comments)
            }
    }
}
