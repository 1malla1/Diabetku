package com.skye.diabetku.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skye.diabetku.data.Result
import com.skye.diabetku.data.remote.response.RegisterResponse
import com.skye.diabetku.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String> get() = _selectedDate


    private val _registerResult = MutableStateFlow<Result<RegisterResponse>>(Result.Loading)
    val registerResult: StateFlow<Result<RegisterResponse>> = _registerResult

    fun setDate(date: Long) {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        _selectedDate.value = dateFormatter.format(Date(date))
    }

    fun register(name: String, email: String, password: String, dateOfBirth: String) {
        viewModelScope.launch {
            try {
                _registerResult.value = Result.Loading
                val response = userRepository.registerUser(name, email, password, dateOfBirth)
                _registerResult.value = Result.Success(response)
            } catch (e: Exception) {
                _registerResult.value = Result.Error(e.message ?: "Unknown error occurred")
            }
        }
    }
}