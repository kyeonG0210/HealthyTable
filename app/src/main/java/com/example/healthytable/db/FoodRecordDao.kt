package com.example.healthytable.db

import androidx.room.*
import com.example.healthytable.model.FoodRecord

@Dao
interface FoodRecordDao {
    @Insert
    suspend fun insert(record: FoodRecord)

    @Query("SELECT * FROM food_records ORDER BY timestamp DESC")
    suspend fun getAll(): List<FoodRecord>

    @Delete
    suspend fun delete(record: FoodRecord)
}
