package com.example.healthytable.ui.community

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthytable.MainActivity
import com.example.healthytable.R
import com.example.healthytable.model.Post
import com.google.firebase.firestore.FirebaseFirestore

class PostWriteActivity : AppCompatActivity() {

    private lateinit var titleEdit: EditText
    private lateinit var contentEdit: EditText
    private lateinit var saveButton: Button
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_write)

        titleEdit = findViewById(R.id.postTitleEdit)
        contentEdit = findViewById(R.id.postContentEdit)
        saveButton = findViewById(R.id.savePostButton)

        saveButton.setOnClickListener {
            val title = titleEdit.text.toString()
            val content = contentEdit.text.toString()

            if (title.isBlank() || content.isBlank()) {
                Toast.makeText(this, "제목과 내용을 입력하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val post = Post(title, content, author = "익명의 유저")
            db.collection("posts")
                .add(post)
                .addOnSuccessListener {
                    Toast.makeText(this, "글이 저장되었습니다", Toast.LENGTH_SHORT).show() // 먼저 토스트 표시
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("navigateToCommunity", true)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                    finish() // finish()는 startActivity 이후에 호출
                }
                .addOnFailureListener {
                    Toast.makeText(this, "업로드 실패", Toast.LENGTH_SHORT).show()
                }
        }
    }
}