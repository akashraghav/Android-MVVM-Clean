package com.raghav.market.helpers

import com.raghav.market.domain.dispatcher.AppDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalCoroutinesApi::class)
class UnconfinedTestingDispatcher : AppDispatcher {

    override fun default(): CoroutineContext {
        return UnconfinedTestDispatcher()
    }

    override fun main(): CoroutineContext {
        return UnconfinedTestDispatcher()
    }

    override fun io(): CoroutineContext {
        return UnconfinedTestDispatcher()
    }

    override fun unconfined(): CoroutineContext {
        return UnconfinedTestDispatcher()
    }
}