package com.example.zestii

data class ConversationItem(
    val conversationId: String,
    val otherUserId: String,
    val lastMessage: Message?
)
