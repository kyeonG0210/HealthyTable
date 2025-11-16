package com.example.healthytable.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthytable.R
import com.example.healthytable.model.Post
import java.text.SimpleDateFormat
import java.util.*

class PostAdapter(
    private val postList: List<Post>,
    private val onLongClick: (Post) -> Unit,
    private val onItemClick: (Post) -> Unit // 추가
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.postTitle)
        val authorText: TextView = itemView.findViewById(R.id.postAuthor)
        val timeText: TextView = itemView.findViewById(R.id.postTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        holder.titleText.text = post.title
        holder.authorText.text = post.content.take(40) + if (post.content.length > 40) "..." else ""

        holder.itemView.setOnClickListener {
            onItemClick(post) // 클릭 이벤트
        }

        holder.itemView.setOnLongClickListener {
            onLongClick(post)
            true
        }

        val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
        holder.timeText.text = sdf.format(Date(post.timestamp))
    }

    override fun getItemCount(): Int = postList.size
}
