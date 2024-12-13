package com.skye.diabetku.data.repository

import com.skye.diabetku.data.Result
import com.skye.diabetku.data.Result.*
import com.skye.diabetku.data.model.LoginRequest
import com.skye.diabetku.data.model.RegisterRequest
import com.skye.diabetku.data.model.UserModel
import com.skye.diabetku.data.pref.UserPreference
import com.skye.diabetku.data.remote.response.GetNotificationResponse
import com.skye.diabetku.data.remote.response.LoginResponse
import com.skye.diabetku.data.remote.response.RegisterResponse
import com.skye.diabetku.data.remote.response.User
import com.skye.diabetku.data.remote.response.VideoItem
import com.skye.diabetku.data.remote.retrofit.ApiConfig
import com.skye.diabetku.data.remote.retrofit.ApiService
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val authRepository: AuthRepository,
    val apiService: ApiService,
) {

    suspend fun registerUser(
        name: String,
        email: String,
        password: String,
        dateOfBirth: String
    ): RegisterResponse {
        val request = RegisterRequest(name, email, password, dateOfBirth)
        return apiService.register(request)
    }

    suspend fun loginUser(
        email: String,
        password: String
    ): LoginResponse {
        val request = LoginRequest(email, password)
        val response = apiService.login(request)

        if (response.status == "success") {
            val loginData = response.data
            val accessToken = loginData?.accessToken.orEmpty()
            val refreshToken = loginData?.refreshToken.orEmpty()
            val user = loginData?.user

            user?.userId?.let { userId ->
                userPreference.saveUserId(userId)
            }

            saveLoginSession(accessToken, refreshToken,user)
            return response
        } else {
            return LoginResponse(message = response.message, status = "error")
        }
    }

    suspend fun saveLoginSession(accessToken: String, refreshToken: String, user: User?) {
        saveUser(UserModel(token = accessToken, isLogin = true))
        saveToken(accessToken)
        saveRefreshToken(refreshToken)

        user?.userId?.let { userId ->
            userPreference.saveUserId(userId)
        }
    }

    suspend fun saveRefreshToken(refreshToken: String) {
        userPreference.saveRefreshToken(refreshToken)
    }

    suspend fun saveUser(user: UserModel) {
        userPreference.saveUser(user)
    }

    suspend fun saveToken(accessToken: String) {
        userPreference.saveAccessToken(accessToken)
    }

    fun getUser(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun getValidAccessToken(): String {
        return authRepository.getAccessTokenOrRefresh() ?: ""
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun getVideoData(): Result<List<VideoItem?>> {
        return try {
            val response = apiService.getVideoData()
            Success(response.video ?: emptyList())
        } catch (e: Exception) {
            Error(e.message ?: "Unknown error")
        }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            authRepository: AuthRepository,
            apiService: ApiService = ApiConfig.getApiService(),
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, authRepository, apiService)
            }.also { instance = it }
    }
}

