package com.skye.diabetku.data.remote.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("data")
	val data: LoginData? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class User(

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("name")
	val name: String? = null
)

data class LoginData(

	@field:SerializedName("user")
	val user: User? = null,

	@field:SerializedName("accessToken")
	val accessToken: String? = null,

	@field:SerializedName("refreshToken")
	val refreshToken: String? = null
)
