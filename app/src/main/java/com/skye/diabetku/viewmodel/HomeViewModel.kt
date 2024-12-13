package com.skye.diabetku.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skye.diabetku.data.Result
import com.skye.diabetku.data.remote.response.ArticleResponse
import com.skye.diabetku.data.remote.response.DataItem
import com.skye.diabetku.data.remote.response.DiabetesCheckResponse
import com.skye.diabetku.data.remote.response.RegisterResponse
import com.skye.diabetku.data.remote.response.VideoItem
import com.skye.diabetku.data.remote.response.VideoResponse
import com.skye.diabetku.data.remote.retrofit.ArticleApiConfig
import com.skye.diabetku.data.repository.UserRepository
import com.skye.diabetku.data.repository.WithAuthRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(
    private val withAuthRepository: WithAuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _articleData = MutableLiveData<ArticleResponse?>()
    val articleData: LiveData<ArticleResponse?> = _articleData

    private val _videoData = MutableLiveData<Result<List<VideoItem?>>>()
    val videoData: LiveData<Result<List<VideoItem?>>> = _videoData

    private val _diabetesResult = MutableLiveData<Result<DiabetesCheckResponse>>()
    val diabetesResult: LiveData<Result<DiabetesCheckResponse>> = _diabetesResult

    private val _dataResult = MutableLiveData<Result<RegisterResponse>>()
    val dataResult: LiveData<Result<RegisterResponse>> = _dataResult

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading


    fun getArticle(query: String, categories: String, language: String, apiKey: String) {
        if (_articleData.value == null) {
            _loading.value = true

            ArticleApiConfig.getApiService().getArticles(query, categories, language, apiKey).enqueue(object :
                Callback<ArticleResponse> {
                override fun onResponse(call: Call<ArticleResponse>, response: Response<ArticleResponse>) {
                    _loading.value = false
                    if (response.isSuccessful) {
                        _articleData.postValue(response.body())
                    } else {
                        _articleData.postValue(null)
                    }
                }

                override fun onFailure(call: Call<ArticleResponse>, t: Throwable) {
                    _loading.value = false
                    _articleData.postValue(null)
                }
            })
        }
    }
    fun getUserData() {
        viewModelScope.launch {
            viewModelScope.launch {
                val result = withAuthRepository.getUserData()
                _dataResult.postValue(result)
            }
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
    fun getVideoData() {
        viewModelScope.launch {
            val result = userRepository.getVideoData()
            _videoData.postValue(result)
        }
    }

}