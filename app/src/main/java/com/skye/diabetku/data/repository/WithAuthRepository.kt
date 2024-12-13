package com.skye.diabetku.data.repository

import android.util.Log
import com.skye.diabetku.data.pref.UserPreference
import com.skye.diabetku.data.remote.response.BloodGlucoseResponse
import com.skye.diabetku.data.remote.retrofit.ApiService
import com.skye.diabetku.data.Result
import com.skye.diabetku.data.Result.Error
import com.skye.diabetku.data.Result.Success
import com.skye.diabetku.data.model.BloodGlucoseRequest
import com.skye.diabetku.data.model.DiabetesCheckRequest
import com.skye.diabetku.data.model.UpdateDataRequest
import com.skye.diabetku.data.model.UpdateGlucoseRequest
import com.skye.diabetku.data.remote.response.BloodGlucoseIdResponse
import com.skye.diabetku.data.remote.response.DiabetesCheckResponse
import com.skye.diabetku.data.remote.response.GetBloodGlucoseResponse
import com.skye.diabetku.data.remote.response.PhotoUploadResponse
import com.skye.diabetku.data.remote.response.RegisterResponse
import okhttp3.MultipartBody

class WithAuthRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
){

    suspend fun putUserData(userData: UpdateDataRequest): Result<RegisterResponse> {
        val userId = userPreference.getUserId()
        return try {
            val response = apiService.updateUserData(userId, userData)
            Success(response)
        } catch (e: Exception) {
            Error(e.message ?: "Unknown error")
        }
    }
    suspend fun getUserData(): Result<RegisterResponse> {
        val userId = userPreference.getUserId()
        return try {
            val response = apiService.getUserData(userId)
            Success(response)
        } catch (e: Exception) {
            Error(e.message ?: "Unknown error")
        }
    }
    suspend fun getBloodGlucose(): Result<GetBloodGlucoseResponse> {
        val userId = userPreference.getUserId()
        return try {
            val response = apiService.getBloodGlucose(userId)
            Success(response)
        } catch (e: Exception) {
            Error(e.message ?: "Unknown error")
        }
    }

    suspend fun addBloodGlucose(request: BloodGlucoseRequest): Result<BloodGlucoseResponse> {
        return try {
            val response = apiService.addBloodGlucose(request)
            Success(response)
        } catch (e: Exception) {
            Error(e.message ?: "Unknown error")
        }
    }

    suspend fun getBloodGlucoseById(id: Int): Result<BloodGlucoseIdResponse> {
        return try {
            val response = apiService.getBloodGlucoseById(id)
            Success(response)
        } catch (e: Exception) {
            Error(e.message ?: "Unknown error")
        }
    }

    suspend fun putBloodGlucoseData(id: Int, glucoseData: UpdateGlucoseRequest): Result<BloodGlucoseIdResponse> {
        val userId = userPreference.getUserId()
        val updatedData = glucoseData.copy(userId = userId)
        return try {
            val response = apiService.putBloodGlucose(id, updatedData)
            Success(response)
        } catch (e: Exception) {
            Error(e.message ?: "Unknown error")
        }
    }

    suspend fun deleteBloodGlucose(id: Int): Result<BloodGlucoseResponse> {
        return try {
            val response = apiService.deleteBloodGlucose(id)
            Log.d("Repository", "Response: ${response}")
            Success(response)
        } catch (e: Exception) {
            Log.e("Repository", "Error: ${e.message}")
            Error(e.message ?: "Unknown error")
        }
    }


    suspend fun checkDiabetes(userId: Int, requestData: DiabetesCheckRequest): Result<DiabetesCheckResponse> {
        return try {
            val response = apiService.checkDiabetes(userId, requestData)
            Success(response)
        } catch (e: Exception) {
            Error(e.message ?: "Unknown error")
        }
    }
    suspend fun getDiabetesData(): Result<DiabetesCheckResponse> {
        val userId = userPreference.getUserId()
        return try {
            val response = apiService.getDiabetesData(userId)
            Success(response)
        } catch (e: Exception) {
            Error(e.message ?: "Unknown error")
        }
    }

    suspend fun uploadProfileImage(photoPart: MultipartBody.Part): Result<PhotoUploadResponse> {
        return try {
            val response = apiService.uploadProfileImage(photoPart)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }
    companion object {
        @Volatile
        private var instance: WithAuthRepository? = null

        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): WithAuthRepository =
            instance ?: synchronized(this) {
                instance ?: WithAuthRepository(apiService, userPreference)
            }.also { instance = it }
    }
}