package com.example.healthytable.model

data class Post(
    val title: String = "",
    val content: String = "",
    val author: String = "",
    val timestamp: Long = System.currentTimeMillis()
)