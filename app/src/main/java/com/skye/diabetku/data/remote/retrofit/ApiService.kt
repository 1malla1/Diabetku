package com.skye.diabetku.data.remote.retrofit

import com.skye.diabetku.data.model.BloodGlucoseRequest
import com.skye.diabetku.data.model.DiabetesCheckRequest
import com.skye.diabetku.data.model.LoginRequest
import com.skye.diabetku.data.model.RefreshTokenRequest
import com.skye.diabetku.data.model.RegisterRequest
import com.skye.diabetku.data.model.UpdateDataRequest
import com.skye.diabetku.data.model.UpdateGlucoseRequest
import com.skye.diabetku.data.remote.response.BloodGlucoseIdResponse
import com.skye.diabetku.data.remote.response.BloodGlucoseResponse
import com.skye.diabetku.data.remote.response.DiabetesCheckResponse
import com.skye.diabetku.data.remote.response.GetBloodGlucoseResponse
import com.skye.diabetku.data.remote.response.GetNotificationResponse
import com.skye.diabetku.data.remote.response.LoginResponse
import com.skye.diabetku.data.remote.response.PhotoUploadResponse
import com.skye.diabetku.data.remote.response.RefreshTokenResponse
import com.skye.diabetku.data.remote.response.RegisterResponse
import com.skye.diabetku.data.remote.response.VideoResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("users/register")
    suspend fun register(
        @Body registerRequest: RegisterRequest
    ): RegisterResponse

    @POST("users/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): LoginResponse

    @PUT("users/{user_id}")
    suspend fun updateUserData(
        @Path("user_id") userId: Int,
        @Body updateDataRequest: UpdateDataRequest
    ):RegisterResponse

    @GET("users/data/{user_id}")
    suspend fun getUserData(
        @Path("user_id") userId: Int
    ): RegisterResponse

    @Multipart
    @POST("users/upload-profile")
    suspend fun uploadProfileImage(
        @Part file: MultipartBody.Part
    ): PhotoUploadResponse

    @POST("users/refresh-token")
    suspend fun refreshToken(
        @Body tokenRequest: RefreshTokenRequest
    ): Response<RefreshTokenResponse>

    @POST("blood-glucose")
    suspend fun addBloodGlucose(
        @Body addBloodGlucoseRequest: BloodGlucoseRequest
    ): BloodGlucoseResponse

    @GET("blood-glucose/all/{user_id}")
    suspend fun getBloodGlucose(
        @Path("user_id") userId: Int
    ): GetBloodGlucoseResponse

    @GET("blood-glucose/{id}")
    suspend fun getBloodGlucoseById(
        @Path("id") id: Int
    ): BloodGlucoseIdResponse

    @PUT("blood-glucose/{id}")
    suspend fun putBloodGlucose(
        @Path("id") id: Int,
        @Body request: UpdateGlucoseRequest
    ): BloodGlucoseIdResponse

    @DELETE("blood-glucose/{id}")
    suspend fun deleteBloodGlucose(
        @Path("id") id: Int
    ): BloodGlucoseResponse

    @POST("diabetes-check/{user_id}")
    suspend fun checkDiabetes(
        @Path("user_id") userId: Int,
        @Body request: DiabetesCheckRequest
    ): DiabetesCheckResponse

    @GET("diabetes-check/{user_id}")
    suspend fun getDiabetesData(
        @Path("user_id") userId: Int
    ): DiabetesCheckResponse

    @GET("videos")
    suspend fun getVideoData(
    ): VideoResponse

    @GET("/notifications/user/{user_id}")
    suspend fun getAllNotifData(
        @Path("user_id") userId: Int
    ): GetNotificationResponse

}
