package com.example.zestii

import Post
import PostAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    private lateinit var postList: MutableList<Post>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        postList = mutableListOf()
        // Add sample posts
        postList.add(Post("User1", "This is the first post"))
        postList.add(Post("User2", "This is another post"))
        postList.add(Post("User3", "Hello world!"))

        postAdapter = PostAdapter(postList)
        recyclerView.adapter = postAdapter
    }
}
