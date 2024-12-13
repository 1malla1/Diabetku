package com.skye.diabetku.data.remote.response

import com.google.gson.annotations.SerializedName

data class DiabetesCheckResponse(

	@field:SerializedName("data")
	val data: GetData? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class GetData(

	@field:SerializedName("pregnancies")
	val pregnancies: Int? = null,

	@field:SerializedName("result")
	val result: String? = null,

	@field:SerializedName("diabetes_pedigree_function")
	val diabetesPedigreeFunction: Any? = null,

	@field:SerializedName("glucose")
	val glucose: Int? = null,

	@field:SerializedName("insulin")
	val insulin: Int? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("blood_pressure")
	val bloodPressure: Int? = null,

	@field:SerializedName("skin_thickness")
	val skinThickness: Int? = null,

	@field:SerializedName("age")
	val age: Int? = null,

	@field:SerializedName("bmi")
	val bmi: Any? = null
)