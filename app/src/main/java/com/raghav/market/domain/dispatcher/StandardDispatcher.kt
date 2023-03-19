package com.raghav.market.domain.dispatcher

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class StandardDispatcher : AppDispatcher {

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        println("CoroutineExceptionHandler got $exception")
    }

    override fun default(): CoroutineContext {
        return Dispatchers.Default + exceptionHandler
    }

    override fun main(): CoroutineContext {
        return Dispatchers.Main.immediate + exceptionHandler
    }

    override fun io(): CoroutineContext {
        return Dispatchers.IO + exceptionHandler
    }

    override fun unconfined(): CoroutineContext {
        return Dispatchers.Unconfined + exceptionHandler
    }

}