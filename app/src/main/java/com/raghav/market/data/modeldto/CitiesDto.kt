package com.raghav.market.data.modeldto

import com.raghav.market.domain.model.CityInfo

data class CitiesDto(
    val name: String,
    val price: Double
)

fun CitiesDto.mapCityInfo() = CityInfo(name, price)

fun toCitiesInfo(citiesDtos: List<CitiesDto>): List<CityInfo> {
    val sellers = ArrayList<CityInfo>()
    citiesDtos.forEach { sellers.add(it.mapCityInfo()) }
    return sellers
}