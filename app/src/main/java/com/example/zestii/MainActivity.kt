package com.example.zestii


import PostAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private lateinit var postList: MutableList<Post>
    private lateinit var db: FirebaseFirestore

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

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            startActivityForResult(intent, CREATE_POST_REQUEST_CODE)
        }

        loadPostsFromDatabase()
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
                val newPost = Post("CurrentUser", it)
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
            }
    }
}
