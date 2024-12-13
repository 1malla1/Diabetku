package com.skye.diabetku.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skye.diabetku.data.Result
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skye.diabetku.data.remote.response.ArticleResponse
import com.skye.diabetku.data.remote.response.DataItem
import com.skye.diabetku.data.remote.response.RegisterData
import com.skye.diabetku.data.remote.response.RegisterResponse
import com.skye.diabetku.data.remote.retrofit.ArticleApiConfig
import com.skye.diabetku.data.repository.UserRepository
import com.skye.diabetku.data.repository.WithAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val withAuthRepository: WithAuthRepository) : ViewModel() {
    private val _logoutResult = MutableStateFlow<Result<Unit>>(Result.Loading)
    val logoutResult: MutableStateFlow<Result<Unit>> = _logoutResult

    private val _dataResult = MutableLiveData<Result<RegisterResponse>>()
    val dataResult: LiveData<Result<RegisterResponse>> = _dataResult

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun logout() {
        viewModelScope.launch {
            _logoutResult.value = Result.Loading
            try {
                userRepository.logout()
                _logoutResult.value = Result.Success(Unit)
            } catch (e: Exception) {
                _logoutResult.value = Result.Error(e.message ?: "Logout failed")
            }
        }
    }

    fun getUserData() {
        viewModelScope.launch {
                val result = withAuthRepository.getUserData()
                _dataResult.postValue(result)
        }

    }

}