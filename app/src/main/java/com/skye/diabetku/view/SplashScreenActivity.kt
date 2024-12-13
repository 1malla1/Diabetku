package com.skye.diabetku.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.skye.diabetku.data.pref.UserPreference
import com.skye.diabetku.data.pref.dataStore
import com.skye.diabetku.data.remote.retrofit.ApiConfig
import com.skye.diabetku.data.repository.AuthRepository
import com.skye.diabetku.data.repository.UserRepository
import com.skye.diabetku.databinding.ActivitySplashScreenBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val apiService = ApiConfig.getApiService()
        val userPreference = UserPreference.getInstance(applicationContext.dataStore)
        val authRepository = AuthRepository.getInstance(userPreference, apiService)
        userRepository = UserRepository.getInstance(userPreference, authRepository)


        Handler(Looper.getMainLooper()).postDelayed({
            checkLoginStatus()
        }, 2000L)

    }

    private fun checkLoginStatus() {
        lifecycleScope.launch {
            try {
                val user = userRepository.getUser().first()
                if (user.isLogin) {
                    // Coba refresh token terlebih dahulu
                    val validToken = userRepository.getValidAccessToken()
                    if (validToken.isNotEmpty()) {
                        delay(2000) // Delay untuk splash screen
                        goToHomeScreen()
                    } else {
                        delay(2000)
                        goToWelcomeScreen()
                    }
                } else {
                    delay(2000)
                    goToWelcomeScreen()
                }
            } catch (e: Exception) {
                Log.e("SplashScreenActivity", "Error checking login status", e)

                delay(2000)
                goToWelcomeScreen()
            }
        }
    }

    private fun goToHomeScreen() {
        val moveIntent = Intent(this@SplashScreenActivity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(moveIntent)
        finish()
    }

    private fun goToWelcomeScreen() {
        val moveIntent = Intent(this@SplashScreenActivity, WelcomeScreenActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(moveIntent)
        finish()
    }
}