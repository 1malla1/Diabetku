package com.skye.diabetku.data.remote.response

import com.google.gson.annotations.SerializedName

data class VideoResponse(

	@field:SerializedName("data")
	val video: List<VideoItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class VideoItem(

	@field:SerializedName("link")
	val link: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("title")
	val title: String? = null
)
