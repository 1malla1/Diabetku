package com.skye.diabetku.data.remote.retrofit

import com.skye.diabetku.data.model.FoodRequest
import com.skye.diabetku.data.remote.response.FoodData
import com.skye.diabetku.data.remote.response.FoodRecommendationResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface FoodApiService {
    @POST("recommend")
    suspend fun getFood(
        @Body foodData: FoodRequest
    ): FoodRecommendationResponse
}