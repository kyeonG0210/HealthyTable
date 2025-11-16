package com.example.healthytable.model

import com.google.gson.annotations.SerializedName

data class FoodItem(
//    val foodNm: String?,
//    val enerc: String?,
//    val sugar: String?,
//    val nat: String?,
//    val fatce: String?,
//    val prot: String?,
//    val chocdf: String?
    @SerializedName("식품코드") val foodCd: String?,
    @SerializedName("식품명") val foodNm: String?,
    @SerializedName("에너지(kcal)") val enerc: String?,
    @SerializedName("당류(g)") val sugar: String?,
    @SerializedName("나트륨(mg)") val nat: String?
)