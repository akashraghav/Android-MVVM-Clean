package com.raghav.market.data.modeldto

import com.raghav.market.domain.model.SellerInfo

data class SellerDto(
    val name: String,
    val id: String
)

fun SellerDto.mapSellerInfo() = SellerInfo(name, id)

fun toSellerInfo(sellerDtos: List<SellerDto>): List<SellerInfo> {
    val sellers = ArrayList<SellerInfo>()
    sellerDtos.forEach { sellers.add(it.mapSellerInfo()) }
    return sellers
}