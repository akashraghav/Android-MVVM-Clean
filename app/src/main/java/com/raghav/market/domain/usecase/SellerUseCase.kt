package com.raghav.market.domain.usecase

import com.raghav.market.domain.model.CityInfo
import com.raghav.market.domain.model.Result
import com.raghav.market.domain.model.SellerInfo
import com.raghav.market.domain.repository.SellerDetailsRepository
import com.raghav.market.domain.utils.retryOnFailure
import com.raghav.market.domain.utils.toPrecision
import javax.inject.Inject
import kotlin.math.absoluteValue

class SellerUseCase @Inject constructor(
    private val sellerDetailsRepository: SellerDetailsRepository,
) {

    suspend fun getCityInfoList(): Result<List<CityInfo>> = retryOnFailure(3) {
        return@retryOnFailure sellerDetailsRepository.getCitiesInfo()
    }

    suspend fun getSellerInfoList(): Result<List<SellerInfo>> = retryOnFailure(3) {
        return@retryOnFailure sellerDetailsRepository.getSellersInfo()
    }

    fun computeGrossPrice(weight: Double, pricePerKg: Double, factor: Double): Double {
        return (weight * pricePerKg * factor).absoluteValue.toPrecision()
    }

}