package com.skye.diabetku.data

import android.util.Log
import com.skye.diabetku.data.pref.UserPreference
import com.skye.diabetku.data.repository.AuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AuthInterceptor (
    private val userPreference: UserPreference,
    private val authRepository: AuthRepository
    ) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        if (isPublicEndpoint(originalRequest)) {
            Log.d("AuthInterceptor", "Public endpoint detected: ${originalRequest.url.encodedPath}")
            return chain.proceed(originalRequest)
        }

        val token = runBlocking {
            authRepository.getAccessTokenOrRefresh()
        }

        return if (token != null) {
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            Log.d("AuthInterceptor", "Adding Authorization header: Bearer $token")
            chain.proceed(newRequest)
        } else {
            Log.w("AuthInterceptor", "Token is null or expired. Proceeding without Authorization header.")
            chain.proceed(originalRequest)
        }
    }
    private fun isPublicEndpoint(request: Request): Boolean {
        val path = request.url.encodedPath
        return path.contains("/login") ||
                path.contains("/register")
    }
}