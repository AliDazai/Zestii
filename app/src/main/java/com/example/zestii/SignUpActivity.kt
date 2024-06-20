package com.example.zestii

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val emailEditText = findViewById<EditText>(R.id.etEmail)
        val passwordEditText = findViewById<EditText>(R.id.etPassword)
        val confirmPasswordEditText = findViewById<EditText>(R.id.etConfirmPassword)
        val usernameEditText = findViewById<EditText>(R.id.etUsername)
        val signUpButton = findViewById<Button>(R.id.btnSignUp)

        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()
            val username = usernameEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || username.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                checkUsernameAndSignUp(email, password, username)
            }
        }
    }

    private fun checkUsernameAndSignUp(email: String, password: String, username: String) {
        db.collection("users").whereEqualTo("username", username).get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    signUpUser(email, password, username)
                } else {
                    Toast.makeText(this, "Username is already taken", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error checking username: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun signUpUser(email: String, password: String, username: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    saveUserToFirestore(email, username)
                } else {
                    Toast.makeText(this, "Sign Up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserToFirestore(email: String, username: String) {
        val user = hashMapOf(
            "email" to email,
            "username" to username
        )

        db.collection("users").add(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Sign Up successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving user: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
