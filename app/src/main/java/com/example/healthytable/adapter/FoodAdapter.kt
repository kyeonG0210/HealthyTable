package com.example.healthytable.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.healthytable.R
import com.example.healthytable.model.FoodItem

class FoodAdapter(
    private val foodList: MutableList<FoodItem>,
    private val onItemClick: (FoodItem) -> Unit
) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_food, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val item = foodList[position]
        holder.bind(item)
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = foodList.size

    fun updateList(newList: List<FoodItem>) {
        val diffCallback = FoodDiffCallback(foodList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        foodList.clear()
        foodList.addAll(newList)

        diffResult.dispatchUpdatesTo(this)
    }

    class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val foodName: TextView = itemView.findViewById(R.id.foodNameText)
        private val calories: TextView = itemView.findViewById(R.id.caloriesText)
        private val sugar: TextView = itemView.findViewById(R.id.sugarText)
        private val sodium: TextView = itemView.findViewById(R.id.sodiumText)

        fun bind(item: FoodItem) {
            foodName.text = item.foodNm ?: "이름 없음"
            calories.text = "열량: ${item.enerc ?: "-"} kcal"
            sugar.text = "당류: ${item.sugar ?: "-"} g"
            sodium.text = "나트륨: ${item.nat ?: "-"} mg"
        }
    }
}
