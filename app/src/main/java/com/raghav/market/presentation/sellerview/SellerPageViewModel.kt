package com.raghav.market.presentation.sellerview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raghav.market.domain.dispatcher.AppDispatcher
import com.raghav.market.domain.model.CityInfo
import com.raghav.market.domain.model.Result
import com.raghav.market.domain.model.SellerInfo
import com.raghav.market.domain.usecase.SellerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SellerPageViewModel @Inject constructor(
    private val sellerUseCase: SellerUseCase,
    private val dispatcher: AppDispatcher
) : ViewModel() {

    companion object {
        const val LOYALTY_FACTOR = 1.12
        const val NO_LOYALTY_FACTOR = 0.98
    }

    private val _loadView = MutableLiveData<Boolean>()
    val loadView: LiveData<Boolean> = _loadView

    private val _factor = MutableLiveData<Double>()
    val factor: LiveData<Double> = _factor

    private val _grossPrice = MutableLiveData<Double>()
    val grossPrice: LiveData<Double> = _grossPrice

    private val sellersMap = HashMap<String, String>()
    private var pricePerKg: Double = 0.0
    var weight: Double = 0.0

    init {
        _factor.postValue(NO_LOYALTY_FACTOR)
    }

    fun loadCities(): LiveData<Result<List<CityInfo>>> {
        _loadView.value = true
        val cities = MutableLiveData<Result<List<CityInfo>>>()
        viewModelScope.launch(dispatcher.io()) {
            val cityInfoResult = sellerUseCase.getCityInfoList()
            cities.postValue(cityInfoResult)
            _loadView.postValue(false)
        }
        return cities
    }

    fun loadSellers(): LiveData<Result<List<SellerInfo>>> {
        val sellers = MutableLiveData<Result<List<SellerInfo>>>()
        viewModelScope.launch(dispatcher.io()) {
            val sellerInfoResult = sellerUseCase.getSellerInfoList()
            sellers.postValue(sellerInfoResult)
            if (sellerInfoResult is Result.Success) {
                sellerInfoResult.data?.forEach { sellersMap[it.name.uppercase()] = it.id.uppercase() }
            }
        }
        return sellers
    }

    fun getSellerId(name: String): String {
        val id = sellersMap[name.uppercase()]
        updateFactor(id != null)
        return id ?: ""
    }

    fun getSellerName(id: String): String {
        val name = sellersMap.filter { it.value == id.uppercase() }.firstNotNullOfOrNull { it.key }
        updateFactor(name != null)
        return name ?: ""
    }

    private fun updateFactor(isRegistered: Boolean) {
        val factor = if (isRegistered) LOYALTY_FACTOR else NO_LOYALTY_FACTOR
        _factor.value = factor
        _grossPrice.value = sellerUseCase.computeGrossPrice(weight, pricePerKg, factor)
    }

    fun updateWeight(weight: Double) {
        this.weight = weight
        _grossPrice.value = sellerUseCase.computeGrossPrice(weight, pricePerKg, factor.value!!)
    }

    fun updatePricePerKg(pricePerKg: Double) {
        this.pricePerKg = pricePerKg
        _grossPrice.value = sellerUseCase.computeGrossPrice(weight, pricePerKg, factor.value!!)
    }
}