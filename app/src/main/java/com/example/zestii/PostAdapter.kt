package com.example.zestii

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class PostAdapter(
    private var posts: MutableList<Post>,
    private val onCommentClick: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position], onCommentClick)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    fun updatePosts(newPosts: List<Post>) {
        posts.clear()
        posts.addAll(newPosts)
        notifyDataSetChanged()
    }

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val postContent: TextView = itemView.findViewById(R.id.postContent)
        private val postUsername: TextView = itemView.findViewById(R.id.postUsername)
        private val postTimestamp: TextView = itemView.findViewById(R.id.postTimestamp)
        private val likeButton: ImageButton = itemView.findViewById(R.id.likeButton)
        private val commentButton: ImageButton = itemView.findViewById(R.id.commentButton)
        private val likeCount: TextView = itemView.findViewById(R.id.likeCount)

        fun bind(post: Post, onCommentClick: (Post) -> Unit) {
            postContent.text = post.content
            postUsername.text = post.username
            postTimestamp.text = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(post.timestamp)
            likeCount.text = post.likes.size.toString()

            likeButton.setImageResource(
                if (post.likes.contains(auth.currentUser?.uid)) R.drawable.ic_like else R.drawable.ic_like
            )

            likeButton.setOnClickListener {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    if (post.likes.contains(userId)) {
                        post.likes.remove(userId)
                    } else {
                        post.likes.add(userId)
                    }
                    db.collection("posts").document(post.postId).update("likes", post.likes)
                }
            }

            commentButton.setOnClickListener {
                onCommentClick(post)
            }

            postUsername.setOnClickListener {
                val intent = Intent(itemView.context, ProfileActivity::class.java)
                intent.putExtra("userId", post.userId)
                itemView.context.startActivity(intent)
            }
        }
    }
}
