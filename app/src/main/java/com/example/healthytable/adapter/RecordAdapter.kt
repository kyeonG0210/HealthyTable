package com.example.healthytable.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthytable.R
import com.example.healthytable.model.FoodRecord


class RecordAdapter(
    private val recordList: List<FoodRecord>,
    private val onItemLongClick: (FoodRecord) -> Unit
) : RecyclerView.Adapter<RecordAdapter.RecordViewHolder>() {

    class RecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameText: TextView = itemView.findViewById(R.id.recordName)
        val infoText: TextView = itemView.findViewById(R.id.recordInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_record, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val record = recordList[position]
        holder.nameText.text = record.name
        holder.infoText.text = "${record.calories}kcal | 당 ${record.sugar}g | 나트륨 ${record.sodium}mg"

        holder.itemView.setOnLongClickListener {
            onItemLongClick(record)
            true
        }
    }

    override fun getItemCount(): Int = recordList.size
}
