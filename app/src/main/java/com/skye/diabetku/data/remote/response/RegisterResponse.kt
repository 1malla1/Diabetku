package com.skye.diabetku.data.remote.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

	@field:SerializedName("data")
	val data: RegisterData? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)
data class RegisterData(

	@field:SerializedName("gender")
	val gender: Any? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("date_of_birth")
	val dateOfBirth: String? = null,


	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("weight")
	val weight: Any? = null,

	@field:SerializedName("profile_image_url")
	val profileImageUrl: Any? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("age")
	val age: Int? = null,

	@field:SerializedName("height")
	val height: Any? = null
)
