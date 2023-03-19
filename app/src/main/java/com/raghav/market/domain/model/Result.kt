package com.raghav.market.domain.model

sealed class Result<T>(val data: T? = null, val message: String? = null, val errorCode: Int = 200) {
    class Success<T>(data: T) : Result<T>(data)
    class Error<T>(message: String, errorCode: Int, data: T? = null) : Result<T>(data, message, errorCode)
    class Loading<T> : Result<T>(null)
}