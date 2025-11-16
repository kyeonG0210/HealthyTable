package com.example.healthytable.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_records")
data class FoodRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val calories: Int,
    val sugar: Double,
    val sodium: Int,
    val timestamp: Long = System.currentTimeMillis()
)
