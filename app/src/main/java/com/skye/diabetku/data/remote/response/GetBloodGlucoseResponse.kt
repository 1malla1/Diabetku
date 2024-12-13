package com.skye.diabetku.data.remote.response

import com.google.gson.annotations.SerializedName

data class GetBloodGlucoseResponse(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataItem(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("formatted_time")
	val formattedTime: String? = null,

	@field:SerializedName("glucose_value")
	val glucoseValue: String? = null,

	@field:SerializedName("formatted_date")
	val formattedDate: String? = null,

	@field:SerializedName("test_type")
	val testType: String? = null
)
