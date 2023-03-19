package com.raghav.market.data.remote.api

import com.raghav.market.data.modeldto.SellerDto
import retrofit2.Response
import retrofit2.http.*

interface SellerDetailsApi {
    companion object {
        const val BASE_URL =
            "https://gist.githubusercontent.com/akashraghav/6a614790ee36c699a2f344b90025caeb/raw/cb0cb2cf4bea01185e7dbfd37f23f5d6375600c7/"
    }

    @GET("cities.json")
    suspend fun getCitiesInfo(): Response<List<com.raghav.market.data.modeldto.CitiesDto>>

    @GET("sellers.json")
    suspend fun getSellersInfo(): Response<List<SellerDto>>

}