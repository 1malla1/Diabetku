package com.skye.diabetku.data.remote.response

import com.google.gson.annotations.SerializedName

data class GetNotificationResponse(

	@field:SerializedName("data")
	val data: List<NotifDataItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class NotifDataItem(

	@field:SerializedName("notification_type")
	val notificationType: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("time_of_day")
	val timeOfDay: List<String?>? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("message")
	val message: String? = null
)
