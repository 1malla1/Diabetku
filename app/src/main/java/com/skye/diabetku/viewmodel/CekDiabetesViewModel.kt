package com.skye.diabetku.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skye.diabetku.data.Result
import com.skye.diabetku.data.model.DiabetesCheckRequest
import com.skye.diabetku.data.pref.UserPreference
import com.skye.diabetku.data.remote.response.DiabetesCheckResponse
import com.skye.diabetku.data.repository.WithAuthRepository
import kotlinx.coroutines.launch

class CekDiabetesViewModel(
    private val withAuthRepository: WithAuthRepository,
    private val userPreference: UserPreference
) : ViewModel() {
    private val _result = MutableLiveData<Result<DiabetesCheckResponse>>()
    val result: MutableLiveData<Result<DiabetesCheckResponse>> get() = _result

    private val _diabetesResult = MutableLiveData<Result<DiabetesCheckResponse>>()
    val diabetesResult: LiveData<Result<DiabetesCheckResponse>> = _diabetesResult


    suspend fun checkDiabetes(
        pregnancies: Int,
        glucose: Int,
        bloodPressure: Int,
        skinThickness: Int,
        insulin: Int,
        bmi: Double,
        diabetesPedigreeFunction: Double,
        age: Int) {
        _result.value = Result.Loading

        try {
            val userId = userPreference.getUserId()
            if (userId == -1) {
                _result.value = Result.Error("User ID tidak valid. Silakan login ulang")
                return
            }

            val request = DiabetesCheckRequest(
                pregnancies = pregnancies,
                glucose = glucose,
                blood_pressure= bloodPressure,
                skin_thickness= skinThickness,
                insulin= insulin,
                bmi= bmi,
                diabetes_pedigree_function = diabetesPedigreeFunction,
                age = age
            )

            _result.value = withAuthRepository.checkDiabetes(userId, request)
        } catch (e: Exception) {
            _result.value = Result.Error(e.message ?: "Unknown error")
        }
    }
    fun getDiabetesCheckData() {
        viewModelScope.launch {
            val result = withAuthRepository.getDiabetesData()
            if (result is Result.Success) {
                _diabetesResult.postValue(result)
            } else {
                Log.e("DiabetesCheck", "Data is null")
            }
        }
    }

}