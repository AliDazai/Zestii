package com.example.zestii

data class Post(
    val username: String = "",
    val content: String = ""
) {
    // No-argument constructor for Firestore
    constructor() : this("", "")
}

