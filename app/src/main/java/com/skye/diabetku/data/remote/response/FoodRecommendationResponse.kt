package com.skye.diabetku.data.remote.response

import com.google.gson.annotations.SerializedName

data class FoodRecommendationResponse(

	@field:SerializedName("data")
	val data: FoodData? = null,

	@field:SerializedName("status")
	val status: Status? = null
)

data class FoodData(

	@field:SerializedName("recommendations")
	val recommendations: String? = null
)

data class Status(

	@field:SerializedName("code")
	val code: Int? = null,

	@field:SerializedName("message")
	val message: String? = null
)
