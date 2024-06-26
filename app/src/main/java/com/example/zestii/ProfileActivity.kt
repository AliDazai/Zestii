package com.example.zestii

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var usernameEditText: EditText
    private lateinit var bioEditText: EditText
    private lateinit var dobEditText: EditText
    private lateinit var saveButton: Button
    private var userId: String? = null
    private var isCurrentUser: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        usernameEditText = findViewById(R.id.etUsername)
        bioEditText = findViewById(R.id.etBio)
        dobEditText = findViewById(R.id.etDob)
        saveButton = findViewById(R.id.btnSave)

        userId = intent.getStringExtra("USER_ID") ?: auth.currentUser?.uid
        isCurrentUser = userId == auth.currentUser?.uid

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
    }

    private fun loadUserProfile() {
        userId?.let {
            db.collection("users").document(it).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        usernameEditText.setText(document.getString("username"))
                        bioEditText.setText(document.getString("bio"))
                        dobEditText.setText(document.getString("dob"))
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error loading profile: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun saveUserProfile() {
        userId?.let { id ->
            val username = usernameEditText.text.toString().trim()
            val bio = bioEditText.text.toString().trim()
            val dob = dobEditText.text.toString().trim()

            db.collection("users").whereEqualTo("username", username).get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty || documents.documents[0].id == id) {
                        val userData = hashMapOf(
                            "username" to username,
                            "bio" to bio,
                            "dob" to dob
                        )

                        db.collection("users").document(id).set(userData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                                navigateToMainActivity()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error updating profile: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this, "Username is already taken", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error checking username: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
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
