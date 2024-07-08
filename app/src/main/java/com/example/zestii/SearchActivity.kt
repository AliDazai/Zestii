package com.example.zestii

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class SearchActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        db = FirebaseFirestore.getInstance()

        val searchField = findViewById<EditText>(R.id.searchField)
        recyclerView = findViewById(R.id.recyclerViewSearch)
        recyclerView.layoutManager = LinearLayoutManager(this)
        searchAdapter = SearchAdapter(mutableListOf()) { user ->
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("userId", user.userId)
            startActivity(intent)
        }
        recyclerView.adapter = searchAdapter

        searchField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchUsers(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun searchUsers(query: String) {
        if (query.isNotEmpty()) {
            db.collection("users")
                .whereGreaterThanOrEqualTo("username", query)
                .get()
                .addOnSuccessListener { result ->
                    val users = mutableListOf<User>()
                    for (document in result) {
                        val user = document.toObject(User::class.java)
                        user.userId = document.id  // Set userId here
                        users.add(user)
                    }
                    searchAdapter.updateUsers(users)
                }
                .addOnFailureListener { e ->
                    Log.e("SearchActivity", "Error searching users: ${e.message}")
                }
        } else {
            searchAdapter.updateUsers(mutableListOf())
        }
    }
}
