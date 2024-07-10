package com.example.zestii

import java.util.Date

data class Message(
    val content: String = "",
    val senderId: String = "",
    val timestamp: Date = Date()
)

