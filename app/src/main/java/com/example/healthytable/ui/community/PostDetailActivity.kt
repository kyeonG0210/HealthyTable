package com.example.healthytable.ui.community

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.healthytable.R
import java.text.SimpleDateFormat
import java.util.*

class PostDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        val titleText = findViewById<TextView>(R.id.detailTitle)
        val contentText = findViewById<TextView>(R.id.detailContent)
        val timeText = findViewById<TextView>(R.id.detailTime)

        // 전달받은 데이터 표시
        val title = intent.getStringExtra("title") ?: ""
        val content = intent.getStringExtra("content") ?: ""
        val timestamp = intent.getLongExtra("timestamp", 0L)

        val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())

        titleText.text = title
        contentText.text = content
        timeText.text = sdf.format(Date(timestamp))
    }
}