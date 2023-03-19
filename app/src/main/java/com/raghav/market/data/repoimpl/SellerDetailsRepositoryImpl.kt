package com.raghav.market.data.repoimpl

import com.raghav.market.data.modeldto.toCitiesInfo
import com.raghav.market.data.modeldto.toSellerInfo
import com.raghav.market.data.remote.api.SellerDetailsApi
import com.raghav.market.domain.model.CityInfo
import com.raghav.market.domain.model.Result
import com.raghav.market.domain.model.SellerInfo
import com.raghav.market.domain.repository.SellerDetailsRepository
import javax.inject.Inject

class SellerDetailsRepositoryImpl @Inject constructor(private val sellerDetailsApi: SellerDetailsApi) : SellerDetailsRepository {

    override suspend fun getCitiesInfo(): Result<List<CityInfo>> {
        val response = sellerDetailsApi.getCitiesInfo()
        return if (response.isSuccessful && response.body() != null) {
            Result.Success(toCitiesInfo(response.body()!!))
        } else {
            Result.Error(response.message(), response.code())
        }
    }

    override suspend fun getSellersInfo(): Result<List<SellerInfo>> {
        val response = sellerDetailsApi.getSellersInfo()
        return if (response.isSuccessful && response.body() != null) {
            Result.Success(toSellerInfo(response.body()!!))
        } else {
            Result.Error(response.message(), response.code())
        }
    }
}