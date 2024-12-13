package com.skye.diabetku.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skye.diabetku.data.Result
import com.skye.diabetku.data.remote.response.LoginResponse
import com.skye.diabetku.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _loginResult = MutableStateFlow<Result<LoginResponse>>(Result.Loading)
    val loginResult: MutableStateFlow<Result<LoginResponse>> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _loginResult.value = Result.Loading
                val response = userRepository.loginUser(email, password)
                if (response.status == "success") {
                    response.data?.let { data ->
                        userRepository.saveLoginSession(
                            data.accessToken ?: "",
                            data.refreshToken ?: "",
                            data.user
                        )
                    }
                    _loginResult.value = Result.Success(response)
                } else {
                    _loginResult.value = Result.Error("Login failed")
                }
            } catch (e: Exception) {
                _loginResult.value = Result.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}