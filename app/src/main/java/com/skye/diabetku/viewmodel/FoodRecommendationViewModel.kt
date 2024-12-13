package com.skye.diabetku.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skye.diabetku.data.model.FoodItem
import com.skye.diabetku.data.model.FoodRecommendationParser
import com.skye.diabetku.data.repository.FoodRepository
import kotlinx.coroutines.launch

class FoodRecommendationViewModel : ViewModel() {
        private val repository = FoodRepository()

        private val _isLoading = MutableLiveData<Boolean>()
        val isLoading: LiveData<Boolean> = _isLoading

        private val _foodRecommendations = MutableLiveData<List<FoodItem>>()
        val foodRecommendations: LiveData<List<FoodItem>> = _foodRecommendations

        private val _error = MutableLiveData<String>()
        val error: LiveData<String> = _error

    fun getFoodRecommendations(glucoseLevel: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = repository.getFoodRecommendations(glucoseLevel)

                result.onSuccess { response ->
                    response.data?.recommendations?.let { recommendations ->
                        val foodList = FoodRecommendationParser.parseRecommendations(recommendations)
                        _foodRecommendations.value = foodList
                    }
                }.onFailure { exception ->
                    _error.value = exception.message ?: "Unknown error occurred"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
}