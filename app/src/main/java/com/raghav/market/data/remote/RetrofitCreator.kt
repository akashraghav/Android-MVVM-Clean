package com.raghav.market.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitCreator(private val baseUrl: String) {

    private fun getClient(): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()
        if (com.raghav.market.BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
            okHttpClient.addInterceptor(interceptor)
        }
        okHttpClient.callTimeout(60 * 1000, TimeUnit.MILLISECONDS)
        return okHttpClient.build()
    }

    fun getInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getClient())
            .build()
    }
}