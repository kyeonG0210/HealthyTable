package com.example.healthytable.ui.record

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthytable.R
import com.example.healthytable.adapter.RecordAdapter
import kotlinx.coroutines.launch
import com.example.healthytable.db.AppDatabase
import android.app.AlertDialog
import com.example.healthytable.model.FoodRecord

class RecordFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_record, container, false)
        recyclerView = view.findViewById(R.id.recordRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(requireContext())
            val recordList = db.foodRecordDao().getAll()

            recyclerView.adapter = RecordAdapter(recordList) { record ->
                AlertDialog.Builder(requireContext())
                    .setTitle("삭제 확인")
                    .setMessage("정말 삭제할까요?")
                    .setPositiveButton("삭제") { _, _ ->
                        lifecycleScope.launch {
                            db.foodRecordDao().delete(record)
                            // 삭제 후 새로고침
                            val updated = db.foodRecordDao().getAll()
                            recyclerView.adapter = RecordAdapter(updated) { newRecord ->
                            }

                        }
                    }
                    .setNegativeButton("취소", null)
                    .show()
            }

        }

        return view
    }
}
