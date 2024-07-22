package com.example.zestii

import android.content.Intent
import android.widget.ImageButton
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var postAdapter: PostAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val newPostButton = findViewById<ImageButton>(R.id.newPostButton)
        val profilePageButton = findViewById<ImageButton>(R.id.profilePageButton)
        val messagesButton = findViewById<ImageButton>(R.id.messagesButton)
        val searchButton = findViewById<ImageButton>(R.id.searchButton)

        newPostButton.setOnClickListener {
            startActivity(Intent(this, CreatePostActivity::class.java))
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

        recyclerView = findViewById(R.id.recyclerViewPosts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        postAdapter = PostAdapter(mutableListOf()) { post ->
            // Handle comment click
            val intent = Intent(this, CommentActivity::class.java)
            intent.putExtra("postId", post.postId)
            startActivity(intent)
        }
        recyclerView.adapter = postAdapter

        loadPosts()
    }

    private fun loadPosts() {
        db.collection("posts")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null || snapshots == null) {
                    return@addSnapshotListener
                }

                val posts = mutableListOf<Post>()
                for (document in snapshots.documents) {
                    val post = document.toObject(Post::class.java)
                    if (post != null) {
                        posts.add(post)
                    }
                }
                postAdapter.updatePosts(posts)
            }
    }
}
