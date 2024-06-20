package com.example.zestii

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class CreatePostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        val postContentEditText: EditText = findViewById(R.id.postContentEditText)
        val postButton: Button = findViewById(R.id.postButton)

        postButton.setOnClickListener {
            val content = postContentEditText.text.toString().trim()
            if (content.isNotEmpty()) {
                // Logic to add the post to the list (this could be saving to a database, etc.)
                // For now, we'll just return to the main activity with the new post content
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("new_post_content", content)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }
}
