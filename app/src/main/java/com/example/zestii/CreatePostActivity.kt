package com.example.zestii

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class CreatePostActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val postContentField = findViewById<EditText>(R.id.postContentField)
        val postButton = findViewById<Button>(R.id.postButton)

        postButton.setOnClickListener {
            val postContent = postContentField.text.toString().trim()
            val userId = auth.currentUser?.uid
            val username = auth.currentUser?.displayName ?: "Unknown"

            if (postContent.isEmpty()) {
                Toast.makeText(this, "Post content cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (postContent.length > 240) {
                Toast.makeText(this, "Post content cannot exceed 240 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userId != null) {
                val postId = db.collection("posts").document().id
                val post = Post(
                    postId = postId,
                    userId = userId,
                    username = username,
                    content = postContent,
                    timestamp = Date()
                )

                db.collection("posts").document(postId).set(post)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Post created successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error creating post: ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.e("CreatePostActivity", "Error creating post", e)
                    }
            }
        }
    }
}
