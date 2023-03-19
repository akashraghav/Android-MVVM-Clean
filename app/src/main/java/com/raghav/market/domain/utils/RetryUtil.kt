package com.raghav.market.domain.utils

import android.util.Log
import com.raghav.market.domain.model.Result
import kotlinx.coroutines.delay

suspend fun <T> retryOnFailure(
    times: Int,
    initialDelayMillis: Long = 0,
    maxDelayMillis: Long = 1000,
    factor: Double = 2.0,
    block: suspend () -> Result<T>
): Result<T> {
    var currentDelay = initialDelayMillis
    repeat(times - 1) {
        delay(currentDelay)
        try {
            val outcome = block()
            if (outcome is Result.Success) {
                return outcome
            } else {
                Log.e("RetryReason", outcome.message.toString())
            }
        } catch (exception: Exception) {
            Log.e("RetryReason", exception.toString())
        }
        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMillis)
    }
    return block()
}

suspend fun <T> retryOnException(
    times: Int,
    initialDelayMillis: Long = 0,
    maxDelayMillis: Long = 1000,
    factor: Double = 2.0,
    block: suspend () -> T
): T {
    var currentDelay = initialDelayMillis
    repeat(times - 1) {
        delay(currentDelay)
        try {
            return block()
        } catch (exception: Exception) {
            Log.e("RetryReason", exception.toString())
        }
        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMillis)
    }
    return block()
}