package com.example.zestii

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String
    private lateinit var currentUserId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val profileName = findViewById<TextView>(R.id.profileName)
        val profileBio = findViewById<TextView>(R.id.profileBio)
        val profileBirthdate = findViewById<TextView>(R.id.profileBirthdate)
        val editProfileButton = findViewById<Button>(R.id.editProfileButton)
        val startChatButton = findViewById<Button>(R.id.startChatButton)

        val currentUser = auth.currentUser
        currentUserId = currentUser?.uid ?: throw IllegalStateException("User is not logged in")

        userId = intent.getStringExtra("userId") ?: throw IllegalStateException("UserId is missing in intent")

        if (userId == currentUserId) {
            // Viewing own profile
            editProfileButton.visibility = View.VISIBLE
            editProfileButton.setOnClickListener {
                val newUsername = findViewById<EditText>(R.id.newUsername).text.toString()
                val newBio = findViewById<EditText>(R.id.newBio).text.toString()
                val newBirthdate = findViewById<EditText>(R.id.newBirthdate).text.toString()

                val updates = hashMapOf<String, Any>(
                    "username" to newUsername,
                    "bio" to newBio,
                    "birthdate" to newBirthdate
                )

                db.collection("users").document(currentUserId)
                    .update(updates)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show()
                        profileName.text = newUsername
                        profileBio.text = newBio
                        profileBirthdate.text = newBirthdate
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error updating profile", Toast.LENGTH_SHORT).show()
                    }
            }
        } else {
            // Viewing someone else's profile
            startChatButton.visibility = View.VISIBLE
            startChatButton.setOnClickListener {
                startChatWithUser(userId)
            }
        }

        db.collection("users").document(userId).get().addOnSuccessListener { document ->
            if (document != null) {
                profileName.text = document.getString("username")
                profileBio.text = document.getString("bio")
                profileBirthdate.text = document.getString("birthdate")
            }
        }
    }

    private fun startChatWithUser(userId: String) {
        val chatId = getChatId(currentUserId, userId)
        val chatData = hashMapOf(
            "participants" to listOf(currentUserId, userId)
        )

        db.collection("chats").document(chatId).set(chatData)
            .addOnSuccessListener {
                val intent = Intent(this, ChatScreenActivity::class.java)
                intent.putExtra("chatId", chatId)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error starting chat", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getChatId(uid1: String, uid2: String): String {
        return if (uid1 < uid2) "$uid1$uid2" else "$uid2$uid1"
    }
}