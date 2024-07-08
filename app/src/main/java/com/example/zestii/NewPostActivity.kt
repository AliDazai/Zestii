package com.example.zestii

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class NewPostActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val postContentField = findViewById<EditText>(R.id.postContentField)
        val postButton = findViewById<Button>(R.id.postButton)

        postButton.setOnClickListener {
            val content = postContentField.text.toString()
            val userId = auth.currentUser?.uid

            if (content.isNotEmpty() && userId != null) {
                val post = hashMapOf(
                    "content" to content,
                    "userId" to userId,
                    "timestamp" to Date()
                )

                db.collection("posts").add(post)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Post created", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error creating post", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Content cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
