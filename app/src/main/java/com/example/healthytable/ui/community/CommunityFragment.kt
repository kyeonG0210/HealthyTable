package com.example.healthytable.ui.community

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthytable.R
import com.example.healthytable.model.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.example.healthytable.adapter.PostAdapter

class CommunityFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostAdapter
    private val postList = mutableListOf<Post>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_community, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.postRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = PostAdapter(postList,
            onLongClick = { post ->
                // 삭제 다이얼로그 그대로 유지
                AlertDialog.Builder(requireContext())
                    .setTitle("게시글 삭제")
                    .setMessage("이 글을 삭제하시겠습니까?")
                    .setPositiveButton("삭제") { _, _ ->
                        db.collection("posts")
                            .whereEqualTo("title", post.title)
                            .whereEqualTo("content", post.content)
                            .whereEqualTo("timestamp", post.timestamp)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                for (doc in querySnapshot) {
                                    db.collection("posts").document(doc.id).delete()
                                        .addOnSuccessListener {
                                            postList.remove(post) // 리스트에서 직접 제거
                                            adapter.notifyDataSetChanged() // RecyclerView 갱신
                                            Toast.makeText(requireContext(), "게시글이 삭제되었습니다", Toast.LENGTH_SHORT).show() // Toast 보장
                                        }

                                }
                            }
                    }
                    .setNegativeButton("취소", null)
                    .show()
            },
            onItemClick = { post -> // 상세보기로 이동
                val intent = Intent(requireContext(), PostDetailActivity::class.java)
                intent.putExtra("title", post.title)
                intent.putExtra("content", post.content)
                intent.putExtra("timestamp", post.timestamp)
                startActivity(intent)
            }
        )

        recyclerView.adapter = adapter

        val writeButton = view.findViewById<ImageButton>(R.id.writePostButton)
        writeButton.setOnClickListener {
            startActivity(Intent(requireContext(), PostWriteActivity::class.java))
        }

        loadPosts()
    }

    private fun loadPosts() {
        db.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                postList.clear()
                for (doc in snapshot ?: return@addSnapshotListener) {
                    val post = doc.toObject(Post::class.java)
                    postList.add(post)
                }
                adapter.notifyDataSetChanged()
            }
    }
}