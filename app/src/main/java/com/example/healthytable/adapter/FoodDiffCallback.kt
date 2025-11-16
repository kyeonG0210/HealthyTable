package com.example.healthytable.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.healthytable.model.FoodItem

class FoodDiffCallback(
    private val oldList: List<FoodItem>,
    private val newList: List<FoodItem>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].foodNm == newList[newItemPosition].foodNm
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
