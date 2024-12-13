package com.skye.diabetku.data.remote.response

import com.google.gson.annotations.SerializedName

data class BloodGlucoseIdResponse(

	@field:SerializedName("data")
	val data: WithIdData? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class WithIdData(

	@field:SerializedName("test_time")
	val testTime: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("glucose_value")
	val glucoseValue: String? = null,

	@field:SerializedName("test_date")
	val testDate: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("test_type")
	val testType: String? = null
)