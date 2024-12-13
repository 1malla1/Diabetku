package com.skye.diabetku.data

sealed class Result<out T> {
    data class Success<out T>(val data: T, val message: String? = null) : Result<T>()
    data class Error(val error: String, val message: String? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
