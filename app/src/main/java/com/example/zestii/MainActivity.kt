package com.example.zestii

import PostAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private lateinit var postList: MutableList<Post>
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var currentUsername: String? = null

    companion object {
        const val CREATE_POST_REQUEST_CODE = 1
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        postList = mutableListOf()
        postAdapter = PostAdapter(postList)
        recyclerView.adapter = postAdapter

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val newPostButton = findViewById<ImageButton>(R.id.newPostButton)
        newPostButton.setOnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            startActivityForResult(intent, CREATE_POST_REQUEST_CODE)
        }

        val searchButton = findViewById<ImageButton>(R.id.searchButton)
        searchButton.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }
        val profileButton = findViewById<ImageButton>(R.id.profileButton)
        profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        loadCurrentUser()
        loadPostsFromDatabase()
    }

    private fun loadCurrentUser() {
        val user = auth.currentUser
        user?.let {
            db.collection("users").whereEqualTo("email", it.email).get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        Log.w(TAG, "No matching documents.")
                        return@addOnSuccessListener
                    }
                    for (document in documents) {
                        currentUsername = document.getString("username")
                        Log.d(TAG, "Current username: $currentUsername")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        }
    }

    private fun loadPostsFromDatabase() {
        db.collection("posts")
            .get()
            .addOnSuccessListener { result ->
                postList.clear()  // Clear the list to avoid duplicates
                for (document in result) {
                    val post = document.toObject(Post::class.java)
                    postList.add(post)
                    Log.d(TAG, "Post added: $post")
                }
                postAdapter.notifyDataSetChanged()
                Log.d(TAG, "Total posts: ${postList.size}")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREATE_POST_REQUEST_CODE && resultCode == RESULT_OK) {
            val newPostContent = data?.getStringExtra("new_post_content")
            newPostContent?.let {
                val username = currentUsername ?: "Unknown User"
                val newPost = Post(username, it)
                savePostToDatabase(newPost)
            }
        }
    }

    private fun savePostToDatabase(post: Post) {
        db.collection("posts")
            .add(post)
            .addOnSuccessListener { documentReference ->
                postList.add(0, post)
                postAdapter.notifyItemInserted(0)
                recyclerView.scrollToPosition(0)
                Log.d(TAG, "Post saved: $post")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                Toast.makeText(this, "Error saving post", Toast.LENGTH_SHORT).show()
            }
    }
}