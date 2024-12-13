package com.skye.diabetku.di

import android.content.Context
import com.skye.diabetku.data.pref.UserPreference
import com.skye.diabetku.data.pref.dataStore
import com.skye.diabetku.data.remote.retrofit.ApiConfig
import com.skye.diabetku.data.repository.AuthRepository
import com.skye.diabetku.data.repository.WithAuthRepository
import com.skye.diabetku.data.repository.UserRepository

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        val authRepository = AuthRepository.getInstance(pref, apiService)
        return UserRepository.getInstance(pref, authRepository, apiService)
    }
    fun provideBloodGlucoseRepository(context: Context): WithAuthRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val authRepository = AuthRepository.getInstance(pref, ApiConfig.getApiService())
        val apiService = ApiConfig.getAuthenticatedApiService(pref, authRepository)
        return WithAuthRepository.getInstance( apiService, pref)
    }
}