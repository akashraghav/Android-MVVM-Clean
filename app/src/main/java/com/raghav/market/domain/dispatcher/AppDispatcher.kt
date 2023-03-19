package com.raghav.market.domain.dispatcher

import kotlin.coroutines.CoroutineContext

interface AppDispatcher {

    fun default(): CoroutineContext
    fun main(): CoroutineContext
    fun io(): CoroutineContext
    fun unconfined(): CoroutineContext

}