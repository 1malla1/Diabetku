package com.skye.diabetku.data.remote.retrofit

import com.skye.diabetku.data.remote.response.ArticleResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ArticleApiService {
    @GET("everything")
    fun getArticles(
        @Query("q") query: String,
        @Query("categories") categories: String,
        @Query("language") language: String,
        @Query("apiKey") apiKey: String
    ): Call<ArticleResponse>
}