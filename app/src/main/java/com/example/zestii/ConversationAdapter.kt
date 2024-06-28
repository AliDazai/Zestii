package com.example.zestii

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ConversationAdapter(
    private val conversations: List<ConversationItem>,
    private val onConversationClick: (ConversationItem) -> Unit
) : RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_conversation, parent, false)
        return ConversationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val conversation = conversations[position]
        holder.bind(conversation, onConversationClick)
    }

    override fun getItemCount() = conversations.size

    class ConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val otherUserTextView: TextView = itemView.findViewById(R.id.otherUserTextView)
        private val lastMessageTextView: TextView = itemView.findViewById(R.id.lastMessageTextView)

        fun bind(conversation: ConversationItem, onConversationClick: (ConversationItem) -> Unit) {
            otherUserTextView.text = conversation.otherUserId
            lastMessageTextView.text = conversation.lastMessage?.message ?: ""
            itemView.setOnClickListener { onConversationClick(conversation) }
        }
    }
}
