package com.skye.diabetku.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skye.diabetku.data.pref.UserPreference
import com.skye.diabetku.data.remote.response.BloodGlucoseResponse
import com.skye.diabetku.data.repository.WithAuthRepository
import java.text.SimpleDateFormat
import com.skye.diabetku.data.Result
import com.skye.diabetku.data.model.BloodGlucoseRequest

import java.util.Date
import java.util.Locale

class AddGlucoseDataViewModel(
    private val withAuthRepository: WithAuthRepository,
    private val userPreference: UserPreference
) : ViewModel() {
    private val _jenisPemeriksaan = MutableLiveData<String>()
    val jenisPemeriksaan: LiveData<String> get() = _jenisPemeriksaan

    private val _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String> get() = _selectedDate

    private val _selectedTime = MutableLiveData<String>()
    val selectedTime: LiveData<String> get() = _selectedTime

    private val _result = MutableLiveData<Result<BloodGlucoseResponse>>()
    val result: LiveData<Result<BloodGlucoseResponse>> = _result

    fun setJenisPemeriksaan(jenisPemeriksaan: String) {
        _jenisPemeriksaan.value = jenisPemeriksaan
    }

    fun setDate(date: Long) {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        _selectedDate.value = dateFormatter.format(Date(date))
    }

    fun setTime(hour: Int, minute: Int) {
        val formattedTime = String.format(Locale.US, "%02d:%02d", hour, minute)
        _selectedTime.value = formattedTime
    }

    suspend fun addGlucoseData(glucoseValue: Int) {
        _result.value = Result.Loading
        try {
            val userId = userPreference.getUserId()
            if (userId == -1) {
                _result.value = Result.Error("User ID tidak valid. Silakan login ulang")
                return
            }

            val request = BloodGlucoseRequest(
                userId = userId,
                glucoseValue = glucoseValue,
                testType = _jenisPemeriksaan.value ?: "",
                testDate = _selectedDate.value ?: "",
                testTime = _selectedTime.value ?: ""
            )

            _result.value = withAuthRepository.addBloodGlucose(request)
        } catch (e: Exception) {
            _result.value = Result.Error(e.message ?: "Unknown error")
        }
    }

}