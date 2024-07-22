package com.example.zestii

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class CommentAdapter(
    private var comments: MutableList<Comment>
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    fun updateComments(newComments: List<Comment>) {
        comments.clear()
        comments.addAll(newComments)
        notifyDataSetChanged()
    }

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val commentContent: TextView = itemView.findViewById(R.id.commentContent)
        private val commentUsername: TextView = itemView.findViewById(R.id.commentUsername)
        private val commentTimestamp: TextView = itemView.findViewById(R.id.commentTimestamp)

        fun bind(comment: Comment) {
            commentContent.text = comment.content
            commentTimestamp.text = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(comment.timestamp)

            db.collection("users").document(comment.userId).get().addOnSuccessListener { document ->
                if (document != null) {
                    val username = document.getString("username") ?: "Unknown"
                    commentUsername.text = username
                    commentUsername.setOnClickListener {
                        val intent = Intent(itemView.context, ProfileActivity::class.java)
                        intent.putExtra("userId", comment.userId)
                        itemView.context.startActivity(intent)
                    }
                }
            }
        }
    }
}