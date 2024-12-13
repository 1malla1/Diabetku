package com.skye.diabetku.data.remote.retrofit


import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ArticleApiConfig {
    companion object {
        private val BASE_URL = "https://newsapi.org/v2/"
        internal const val API_KEY = "e5a48933cfbc4867869fe287c7d4c54a"

        fun getApiService(): ArticleApiService {
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ArticleApiService::class.java)
        }
    }
}