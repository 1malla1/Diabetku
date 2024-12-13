package com.skye.diabetku.data.remote.response

import com.google.gson.annotations.SerializedName

data class PhotoUploadResponse(

	@field:SerializedName("data")
	val data: PhotoData? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class PhotoData(

	@field:SerializedName("user")
	val user: PhotoUser? = null,

	@field:SerializedName("url")
	val url: String? = null
)

data class PhotoUser(

	@field:SerializedName("gender")
	val gender: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("date_of_birth")
	val dateOfBirth: String? = null,

	@field:SerializedName("password_hash")
	val passwordHash: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("weight")
	val weight: Any? = null,

	@field:SerializedName("profile_image_url")
	val profileImageUrl: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("age")
	val age: Int? = null,

	@field:SerializedName("height")
	val height: Any? = null
)
