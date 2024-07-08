package com.example.zestii

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class ChatScreenActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var chatId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_screen)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        chatId = intent.getStringExtra("chatId") ?: ""

        recyclerView = findViewById(R.id.recyclerViewChat)
        recyclerView.layoutManager = LinearLayoutManager(this)
        chatAdapter = ChatAdapter(mutableListOf())
        recyclerView.adapter = chatAdapter

        val messageField = findViewById<EditText>(R.id.messageField)
        val sendButton = findViewById<Button>(R.id.sendButton)

        sendButton.setOnClickListener {
            val messageContent = messageField.text.toString()
            val userId = auth.currentUser?.uid

            if (messageContent.isNotEmpty() && userId != null) {
                val message = hashMapOf(
                    "content" to messageContent,
                    "senderId" to userId,
                    "timestamp" to Date()
                )

                db.collection("chats").document(chatId).collection("messages").add(message)
                messageField.text.clear()
            }
        }

        loadMessages()
    }

    private fun loadMessages() {
        db.collection("chats").document(chatId).collection("messages")
            .orderBy("timestamp").addSnapshotListener { snapshots, e ->
                if (e != null || snapshots == null) {
                    return@addSnapshotListener
                }

                val messages = mutableListOf<Message>()
                for (document in snapshots.documents) {
                    val message = document.toObject(Message::class.java)
                    if (message != null) {
                        messages.add(message)
                    }
                }
                chatAdapter.updateMessages(messages)
            }
    }
}
