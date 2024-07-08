package com.example.zestii

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val newPostButton = findViewById<Button>(R.id.newPostButton)
        val profilePageButton = findViewById<Button>(R.id.profilePageButton)
        val messagesButton = findViewById<Button>(R.id.messagesButton)
        val searchButton = findViewById<Button>(R.id.searchButton)

        newPostButton.setOnClickListener {
            // Implement New Post functionality
        }

        profilePageButton.setOnClickListener {
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("userId", currentUser.uid)
                startActivity(intent)
            }
        }

        messagesButton.setOnClickListener {
            startActivity(Intent(this, MessagesActivity::class.java))
        }

        searchButton.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
    }
}