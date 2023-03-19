package com.raghav.market.domain.repository

import com.raghav.market.domain.model.CityInfo
import com.raghav.market.domain.model.Result
import com.raghav.market.domain.model.SellerInfo

interface SellerDetailsRepository {
    suspend fun getCitiesInfo(): Result<List<CityInfo>>
    suspend fun getSellersInfo(): Result<List<SellerInfo>>
}