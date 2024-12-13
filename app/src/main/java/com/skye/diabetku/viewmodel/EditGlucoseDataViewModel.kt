package com.skye.diabetku.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skye.diabetku.data.Result
import com.skye.diabetku.data.model.UpdateGlucoseRequest
import com.skye.diabetku.data.pref.UserPreference
import com.skye.diabetku.data.remote.response.BloodGlucoseIdResponse
import com.skye.diabetku.data.remote.response.BloodGlucoseResponse
import com.skye.diabetku.data.repository.WithAuthRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditGlucoseDataViewModel(
    private val withAuthRepository: WithAuthRepository,
    private val userPreference: UserPreference): ViewModel() {

    private val _jenis = MutableLiveData<String>()
    val jenis: LiveData<String> get() = _jenis

    private val _selectedTime = MutableLiveData<String>()
    val selectedTime: LiveData<String> get() = _selectedTime

    private val _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String> get() = _selectedDate

    private val _updateBloodGlucose = MutableLiveData<Result<BloodGlucoseIdResponse>>()
    val updateBloodGlucose: LiveData<Result<BloodGlucoseIdResponse>> get() = _updateBloodGlucose

    private val _deleteBloodGlucose = MutableLiveData<Result<BloodGlucoseResponse>>()
    val deleteBloodGlucose: LiveData<Result<BloodGlucoseResponse>> get() = _deleteBloodGlucose


    private val _bloodGlucoseDatabyId = MutableLiveData<BloodGlucoseIdResponse?>()
    val bloodGlucoseDataById: MutableLiveData<BloodGlucoseIdResponse?> = _bloodGlucoseDatabyId

    fun getBloodGlucoseById(id: Int) {
        viewModelScope.launch {
            val result = withAuthRepository.getBloodGlucoseById(id)
            if (result is Result.Success) {
                _bloodGlucoseDatabyId.value = result.data
            } else {
                _bloodGlucoseDatabyId.value = null
            }
        }
    }
    fun updateBloodGlucose(
        glucose: Double,
        tanggal: String,
        waktu: String,
        jenisPemeriksaan : String,
        id: Int
    ) {
        viewModelScope.launch {
            _updateBloodGlucose.value = Result.Loading
            try {
                val glucoseData = UpdateGlucoseRequest(
                    glucoseValue = glucose,
                    testTime = waktu,
                    testDate = tanggal,
                    testType = jenisPemeriksaan,
                    userId = userPreference.getUserId()
                )
                val result = withAuthRepository.putBloodGlucoseData(id, glucoseData)
                _updateBloodGlucose.value = result
            } catch (e: Exception) {
                _updateBloodGlucose.value = Result.Error(e.message ?: "Unknown error")
            }
        }
    }
    fun deleteBloodGlucose(id: Int) {
        viewModelScope.launch {
            _deleteBloodGlucose.value = Result.Loading
            try {
                val result = withAuthRepository.deleteBloodGlucose(id)
                _deleteBloodGlucose.value = result
            } catch (e: Exception) {
                _deleteBloodGlucose.value = Result.Error(e.message ?: "Unknown error")
            }
        }
    }


    fun setJenisPemeriksaan(testType: String) {
        _jenis.value = testType
    }

    fun setDate(date: Long) {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        _selectedDate.value = dateFormatter.format(Date(date))
    }

    fun setTime(hour: Int, minute: Int) {
        val formattedTime = String.format(Locale.US, "%02d:%02d", hour, minute)
        _selectedTime.value = formattedTime
    }
}