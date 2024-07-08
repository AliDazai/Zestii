package com.example.zestii

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MessagesActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        recyclerView = findViewById(R.id.recyclerViewMessages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        messageAdapter = MessageAdapter(mutableListOf()) { chatId ->
            val intent = Intent(this, ChatScreenActivity::class.java)
            intent.putExtra("chatId", chatId)
            startActivity(intent)
        }
        recyclerView.adapter = messageAdapter

        loadMessages()
    }

    private fun loadMessages() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("chats").whereArrayContains("participants", userId).get()
                .addOnSuccessListener { result ->
                    val chats = mutableListOf<Chat>()
                    for (document in result) {
                        val chat = document.toObject(Chat::class.java)
                        chats.add(chat)
                    }
                    messageAdapter.updateChats(chats)
                }
        }
    }
}
