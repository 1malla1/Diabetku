package com.skye.diabetku.data.remote.response

import com.google.gson.annotations.SerializedName

data class RefreshTokenResponse(
    @field:SerializedName("accessToken")
    val accessToken: String? = null
)
