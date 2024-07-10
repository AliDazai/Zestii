package com.example.zestii

import java.util.Date

data class Post(
    val postId: String = "",
    val userId: String = "",
    val username: String = "",
    val content: String = "",
    val timestamp: Date = Date(),
    val likes: MutableList<String> = mutableListOf(),
    val comments: MutableList<Comment> = mutableListOf()
)

data class Comment(
    val userId: String = "",
    val username: String = "",
    val content: String = "",
    val timestamp: Date = Date()
)
