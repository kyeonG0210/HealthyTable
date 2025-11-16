package com.example.healthytable.ui.food

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthytable.R
import com.example.healthytable.adapter.FoodAdapter
import com.example.healthytable.db.AppDatabase
import com.example.healthytable.model.FoodItem
import com.example.healthytable.model.FoodRecord
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*

class FoodListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FoodAdapter
    private lateinit var searchEditText: EditText
    private lateinit var sortSpinner: Spinner

    private var fullFoodList: List<FoodItem> = listOf()
    private var isDataLoaded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_food_list, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        searchEditText = view.findViewById(R.id.searchEditText)
        sortSpinner = view.findViewById(R.id.sortSpinner)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = FoodAdapter(mutableListOf()) { foodItem ->
            Toast.makeText(requireContext(), "${foodItem.foodNm} 추가됨", Toast.LENGTH_SHORT).show()

            val record = FoodRecord(
                name = foodItem.foodNm ?: "이름 없음",
                calories = foodItem.enerc?.toIntOrNull() ?: 0,
                sugar = foodItem.sugar?.toDoubleOrNull() ?: 0.0,
                sodium = foodItem.nat?.toIntOrNull() ?: 0
            )

            lifecycleScope.launch {
                val db = AppDatabase.getDatabase(requireContext())
                db.foodRecordDao().insert(record)
            }
        }
        recyclerView.adapter = adapter

        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                applySortingAndFiltering()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                applySortingAndFiltering()
            }
        })

        fetchFoodsFromAsset()

        return view
    }

    private fun applySortingAndFiltering() {
        if (!isDataLoaded) return

        val keyword = searchEditText.text.toString()
        val position = sortSpinner.selectedItemPosition

        val sorted = when (position) {
            1 -> fullFoodList.sortedBy { it.sugar?.toDoubleOrNull() ?: Double.MAX_VALUE }
            2 -> fullFoodList.sortedBy { it.nat?.toIntOrNull() ?: Int.MAX_VALUE }
            3 -> fullFoodList.sortedBy { it.enerc?.toIntOrNull() ?: Int.MAX_VALUE }
            else -> fullFoodList
        }

        val filtered = if (keyword.isNotBlank()) {
            sorted.filter { it.foodNm?.contains(keyword, ignoreCase = true) == true }
        } else {
            sorted
        }

        recyclerView.post {
            adapter.updateList(filtered)
        }
    }

    private fun fetchFoodsFromAsset() {
        lifecycleScope.launch {
            try {
                val parsedList = withContext(Dispatchers.IO) {
                    val jsonString = requireContext().assets.open("food_data.json")
                        .bufferedReader().use { it.readText() }

                    val jsonObject = Gson().fromJson<Map<String, Any>>(jsonString, object : TypeToken<Map<String, Any>>() {}.type)

                    // records 항목을 직접 리스트로 변환
                    val records = jsonObject["records"] ?: emptyList<Any>()
                    val recordsJson = Gson().toJson(records) // <- 이 부분 유지
                    val type = object : TypeToken<List<FoodItem>>() {}.type
                    val list = Gson().fromJson<List<FoodItem>>(recordsJson, type)

                    list.take(1000) // 너무 많으면 OOM → 제한 걸기
                }

                fullFoodList = parsedList.filter { it.foodNm != null }
                isDataLoaded = true
                applySortingAndFiltering()

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "로딩 실패: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

}