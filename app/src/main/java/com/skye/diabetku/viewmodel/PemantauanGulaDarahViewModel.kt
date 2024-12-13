package com.skye.diabetku.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skye.diabetku.data.repository.WithAuthRepository
import kotlinx.coroutines.launch
import com.skye.diabetku.data.Result
import com.skye.diabetku.data.remote.response.BloodGlucoseIdResponse
import com.skye.diabetku.data.remote.response.DataItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PemantauanGulaDarahViewModel(private val withAuthRepository: WithAuthRepository): ViewModel() {

    private val _jenis = MutableLiveData<String>()
    val jenis: LiveData<String> get() = _jenis

    private val _selectedDate = MutableLiveData<String>()
    val selectedDate: LiveData<String> get() = _selectedDate

    private val _result = MutableLiveData<List<DataItem>?>()
    val result: MutableLiveData<List<DataItem>?> = _result

    fun getBloodGlucose() {
        viewModelScope.launch {
            val response = withAuthRepository.getBloodGlucose()
            if (response is Result.Success) {
                _result.value = response.data.data as List<DataItem>?
            } else {
                Log.e("BloodGlucose", "Data is null")
            }
        }
    }
}
