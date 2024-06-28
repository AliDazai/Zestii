package com.example.zestii

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var messageList: MutableList<Message>
    private var currentUserId: String? = null
    private var otherUserId: String? = null
    private var conversationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().reference

        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView)

        messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        messageList = mutableListOf()
        chatAdapter = ChatAdapter(messageList, auth.currentUser?.uid ?: "")
        messagesRecyclerView.adapter = chatAdapter

        currentUserId = auth.currentUser?.uid
        otherUserId = intent.getStringExtra("OTHER_USER_ID") // Retrieve the other user ID from the intent

        Log.d("ChatActivity", "currentUserId: $currentUserId, otherUserId: $otherUserId")

        conversationId = getConversationId(currentUserId!!, otherUserId!!)

        sendButton.setOnClickListener {
            sendMessage()
        }

        loadMessages()
    }

    private fun loadMessages() {
        db.child("conversations").child(conversationId!!).child("messages")
            .orderByChild("timestamp")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (messageSnapshot in snapshot.children) {
                        val message = messageSnapshot.getValue(Message::class.java)
                        if (message != null) {
                            messageList.add(message)
                        }
                    }
                    chatAdapter.notifyDataSetChanged()
                    messagesRecyclerView.scrollToPosition(messageList.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ChatActivity", "Load messages cancelled", error.toException())
                }
            })
    }

    private fun sendMessage() {
        val messageText = messageEditText.text.toString().trim()
        if (messageText.isEmpty()) {
            return
        }

        val message = Message(
            senderId = currentUserId!!,
            receiverId = otherUserId!!,
            message = messageText,
            timestamp = System.currentTimeMillis()
        )

        db.child("conversations").child(conversationId!!).child("messages").push().setValue(message)
            .addOnSuccessListener {
                Log.d("ChatActivity", "Message sent successfully")
            }
            .addOnFailureListener { e ->
                Log.e("ChatActivity", "Error sending message", e)
            }

        messageEditText.text.clear()
    }
}
