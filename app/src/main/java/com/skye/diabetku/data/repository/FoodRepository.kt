package com.skye.diabetku.data.repository

import com.skye.diabetku.data.model.FoodRequest
import com.skye.diabetku.data.remote.response.FoodRecommendationResponse
import com.skye.diabetku.data.remote.retrofit.FoodApiConfig

class FoodRepository {
    private val foodApiService = FoodApiConfig.getFoodApiService()

    suspend fun getFoodRecommendations(glucoseLevel: Int): Result<FoodRecommendationResponse> {
        return try {
            val foodRecommendation = FoodRequest(glucose_level = glucoseLevel)
            val response = foodApiService.getFood(foodRecommendation)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}