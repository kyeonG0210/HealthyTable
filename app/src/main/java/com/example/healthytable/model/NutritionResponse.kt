package com.example.healthytable.model

data class NutritionResponse(
    val response: ResponseWrapper
)

data class ResponseWrapper(
    val body: ResponseBody
)

data class ResponseBody(
    val items: List<FoodItem>
)
