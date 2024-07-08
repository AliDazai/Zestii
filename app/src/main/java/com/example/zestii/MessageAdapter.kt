package com.example.zestii

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter(private var chats: MutableList<Chat>, private val onClick: (String) -> Unit) : RecyclerView.Adapter<MessageAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chats[position], onClick)
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    fun updateChats(newChats: List<Chat>) {
        chats.clear()
        chats.addAll(newChats)
        notifyDataSetChanged()
    }

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val chatName: TextView = itemView.findViewById(R.id.chatName)

        fun bind(chat: Chat, onClick: (String) -> Unit) {
            chatName.text = chat.chatId
            itemView.setOnClickListener {
                onClick(chat.chatId)
            }
        }
    }
}
