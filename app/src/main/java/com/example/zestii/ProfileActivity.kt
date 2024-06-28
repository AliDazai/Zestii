package com.example.zestii

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference
    private lateinit var usernameEditText: EditText
    private lateinit var bioEditText: EditText
    private lateinit var dobEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var messageButton: Button
    private var userId: String? = null
    private var isCurrentUser: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().reference

        usernameEditText = findViewById(R.id.etUsername)
        bioEditText = findViewById(R.id.etBio)
        dobEditText = findViewById(R.id.etDob)
        saveButton = findViewById(R.id.btnSave)
        messageButton = findViewById(R.id.btnMessage)

        userId = intent.getStringExtra("USER_ID") ?: auth.currentUser?.uid
        isCurrentUser = userId == auth.currentUser?.uid

        Log.d("ProfileActivity", "Viewing profile of userId: $userId")

        if (!isCurrentUser) {
            disableEditing()
        }

        loadUserProfile()

        saveButton.setOnClickListener {
            if (isCurrentUser) {
                saveUserProfile()
            } else {
                Toast.makeText(this, "You can only edit your own profile", Toast.LENGTH_SHORT).show()
            }
        }

        messageButton.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("OTHER_USER_ID", userId)
            Log.d("ProfileActivity", "Passing userId: $userId to ChatActivity")
            startActivity(intent)
        }
    }

    private fun loadUserProfile() {
        userId?.let {
            db.child("users").child(it).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        usernameEditText.setText(snapshot.child("username").value as String?)
                        bioEditText.setText(snapshot.child("bio").value as String?)
                        dobEditText.setText(snapshot.child("dob").value as String?)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ProfileActivity, "Error loading profile: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun saveUserProfile() {
        userId?.let { id ->
            val username = usernameEditText.text.toString().trim()
            val bio = bioEditText.text.toString().trim()
            val dob = dobEditText.text.toString().trim()

            db.child("users").orderByChild("username").equalTo(username).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists() || snapshot.children.first().key == id) {
                        val userData = mapOf(
                            "username" to username,
                            "bio" to bio,
                            "dob" to dob
                        )

                        db.child("users").child(id).setValue(userData)
                            .addOnSuccessListener {
                                Toast.makeText(this@ProfileActivity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                                navigateToMainActivity()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this@ProfileActivity, "Error updating profile: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this@ProfileActivity, "Username is already taken", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ProfileActivity, "Error checking username: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun disableEditing() {
        usernameEditText.isEnabled = false
        bioEditText.isEnabled = false
        dobEditText.isEnabled = false
        saveButton.isEnabled = false
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
