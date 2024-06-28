package com.example.zestii

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ConversationsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference
    private lateinit var conversationsRecyclerView: RecyclerView
    private lateinit var conversationAdapter: ConversationAdapter
    private lateinit var conversationList: MutableList<ConversationItem>
    private var currentUserId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversations)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().reference

        conversationsRecyclerView = findViewById(R.id.conversationsRecyclerView)
        conversationsRecyclerView.layoutManager = LinearLayoutManager(this)

        conversationList = mutableListOf()
        conversationAdapter = ConversationAdapter(conversationList) { conversation ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("OTHER_USER_ID", conversation.otherUserId)
            startActivity(intent)
        }
        conversationsRecyclerView.adapter = conversationAdapter

        currentUserId = auth.currentUser?.uid
        loadConversations()
    }

    private fun loadConversations() {
        db.child("conversations").orderByChild("participants/$currentUserId").equalTo(true)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    conversationList.clear()
                    for (conversationSnapshot in snapshot.children) {
                        val conversationId = conversationSnapshot.key ?: continue
                        val participants = conversationSnapshot.child("participants").value as Map<String, Boolean>
                        val otherUserId = participants.keys.first { it != currentUserId }
                        val lastMessage = conversationSnapshot.child("lastMessage").getValue(Message::class.java)
                        conversationList.add(ConversationItem(conversationId, otherUserId, lastMessage))
                    }
                    conversationAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }
}
