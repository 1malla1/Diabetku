package com.skye.diabetku.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.skye.diabetku.data.remote.response.RegisterResponse
import com.skye.diabetku.data.repository.WithAuthRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import com.skye.diabetku.data.Result
import com.skye.diabetku.data.model.UpdateDataRequest
import okhttp3.MultipartBody
import java.util.Locale

class UbahProfileViewModel(private val withAuthRepository: WithAuthRepository) : ViewModel() {
    private val _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String> get() = _selectedDate

    private val _jenisKelamin = MutableLiveData<String>()
    val jenisKelamin: LiveData<String> get() = _jenisKelamin

    private val _dataResult = MutableLiveData<Result<RegisterResponse>>()
    val dataResult: LiveData<Result<RegisterResponse>> = _dataResult

    private val _updateDataResult = MutableLiveData<Result<RegisterResponse>>()
    val updateDataResult: LiveData<Result<RegisterResponse>> = _updateDataResult

    private val _uploadResult = MutableLiveData<Result<String>>()
    val uploadResult: LiveData<Result<String>> get() = _uploadResult

    fun uploadProfileImage(photoPart: MultipartBody.Part): LiveData<Result<String>> = liveData {
        emit(Result.Loading)
        try {
            val response = withAuthRepository.uploadProfileImage(photoPart)
            if (response is Result.Success) {
                val imageUrl = response.data?.data?.url
                emit(Result.Success(imageUrl ?: "Unknown URL"))
            } else if (response is Result.Error) {
                emit(Result.Error(response.message ?: "Error uploading image"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.localizedMessage ?: "Unknown error"))
        }
    }


    fun setDate(date: Long) {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        _selectedDate.value = dateFormatter.format(Date(date))
    }

    fun setJenisKelamin(jenisKelamin: String) {
        _jenisKelamin.value = jenisKelamin
    }

    fun getUserData() {
        viewModelScope.launch {
            _dataResult.value = Result.Loading
            try {
                val result = withAuthRepository.getUserData()
                _dataResult.value = result
            } catch (e: Exception) {
                _dataResult.value = Result.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun updateUserData(
        name: String,
        email: String,
        dateOfBirth: String,
        gender: String,
        height: Double,
        weight: Double
    ) {
        viewModelScope.launch {
            _updateDataResult.value = Result.Loading
            try {
                val userData = UpdateDataRequest(
                    name = name,
                    email = email,
                    date_of_birth = dateOfBirth,
                    gender = gender,
                    height = height,
                    weight = weight
                )
                val result = withAuthRepository.putUserData(userData)
                _updateDataResult.value = result
            } catch (e: Exception) {
                _updateDataResult.value = Result.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }
}
