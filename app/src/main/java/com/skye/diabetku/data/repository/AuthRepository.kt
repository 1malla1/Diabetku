package com.skye.diabetku.data.repository

import com.auth0.android.jwt.DecodeException
import com.auth0.android.jwt.JWT
import com.skye.diabetku.data.Result
import com.skye.diabetku.data.model.RefreshTokenRequest
import com.skye.diabetku.data.pref.UserPreference
import com.skye.diabetku.data.remote.retrofit.ApiConfig
import com.skye.diabetku.data.remote.retrofit.ApiService
import java.util.Date

class AuthRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    suspend fun getAccessTokenOrRefresh(): String? {
        return try {
            val accessToken = getAccessTokenFromLocalStorage()
            if (accessToken.isEmpty() || isTokenExpired(accessToken)) {
                val refreshToken = getRefreshTokenFromLocalStorage()
                if (refreshToken.isNotEmpty()) {
                    refreshAccessToken(refreshToken).let { result ->
                        when (result) {
                            is Result.Success -> result.data
                            else -> null
                        }
                    }
                } else null
            } else accessToken
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getAccessTokenFromLocalStorage(): String {
        return userPreference.getAccessToken()
    }

    private fun isTokenExpired(accessToken: String): Boolean {
        if (accessToken.isEmpty()) return true
        return try {
            val jwt = JWT(accessToken)
            val expirationTime = jwt.expiresAt
            expirationTime?.before(Date()) ?: true
        } catch (e: DecodeException) {
            true
        }
    }

    private suspend fun getRefreshTokenFromLocalStorage(): String {
        return userPreference.getRefreshToken()
    }

    private suspend fun refreshAccessToken(refreshToken: String): Result<String> {
        return try {
            val response = apiService.refreshToken(RefreshTokenRequest(refreshToken))
            if (response.isSuccessful && response.body()?.accessToken != null) {
                val newToken = response.body()!!.accessToken!!
                userPreference.saveAccessToken(newToken)
                Result.Success(newToken)
            } else {
                Result.Error("Failed to refresh token")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    companion object {
        @Volatile
        private var instance: AuthRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService = ApiConfig.getApiService()
        ): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(userPreference, apiService)
            }.also { instance = it }
    }


}
