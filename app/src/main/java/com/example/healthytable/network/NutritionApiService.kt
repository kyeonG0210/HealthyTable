package com.example.healthytable.network

import com.example.healthytable.model.NutritionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NutritionApiService {
    @GET("openapi/tn_pubr_public_nutri_food_info_api")
    suspend fun getFoods(
        @Query("serviceKey") serviceKey: String,
        @Query("pageNo") pageNo: Int = 1,
        @Query("numOfRows") numOfRows: Int = 100,
        @Query("type") type: String = "json"
    ): Response<NutritionResponse>
}
