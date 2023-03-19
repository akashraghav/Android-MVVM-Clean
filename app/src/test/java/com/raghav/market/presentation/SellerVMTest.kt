package com.raghav.market.presentation

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.raghav.market.domain.dispatcher.AppDispatcher
import com.raghav.market.domain.model.CityInfo
import com.raghav.market.domain.model.Result
import com.raghav.market.domain.model.SellerInfo
import com.raghav.market.domain.repository.SellerDetailsRepository
import com.raghav.market.domain.usecase.SellerUseCase
import com.raghav.market.helpers.UnconfinedTestingDispatcher
import com.raghav.market.presentation.sellerview.SellerPageViewModel
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
@DelicateCoroutinesApi
class SellerVMTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")


    private lateinit var repository: SellerDetailsRepository
    private lateinit var dispatcher: AppDispatcher
    private lateinit var useCase: SellerUseCase
    private lateinit var subject: SellerPageViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(relaxed = true)
        Dispatchers.setMain(mainThreadSurrogate)

        mockkStatic(Log::class)
        every { Log.v(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        repository = mockk()
        coEvery { repository.getSellersInfo() } returns Result.Success(generatesellerList())
        coEvery { repository.getCitiesInfo() } returns Result.Success(generatecityList())
        useCase = spyk(SellerUseCase(repository))
        dispatcher = UnconfinedTestingDispatcher()
        subject = SellerPageViewModel(useCase, dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    private fun generatesellerList(): List<SellerInfo> {
        val farmerList = ArrayList<SellerInfo>()
        farmerList.add(SellerInfo("Rajesh", "S1234"))
        farmerList.add(SellerInfo("Suresh", "S5424"))
        return farmerList
    }

    private fun generatecityList(): List<CityInfo> {
        val cityList = ArrayList<CityInfo>()
        cityList.add(CityInfo("Shimla", 123.23))
        cityList.add(CityInfo("Kasol", 119.23))
        return cityList
    }

    @Test
    fun `test load cities call success`() = runTest {
        subject.loadCities().observeForever {
            Truth.assertThat(it.data).isNotNull()
        }
    }

    @Test
    fun `test load cities call failure`() = runTest {
        coEvery { useCase.getCityInfoList() } returns Result.Error("Failed", 400)
        subject.loadCities().observeForever {
            Truth.assertThat(it.data).isNull()
            Truth.assertThat(it.message).isEqualTo("Failed")
        }
    }

    @Test
    fun `test load farmers call success`() = runTest {
        Truth.assertThat(subject.loadSellers().value).isNotNull()
    }

    @Test
    fun `test load farmers call failure`() = runTest {
        coEvery { useCase.getSellerInfoList() } returns Result.Error("Failed", 400)
        subject.loadSellers().observeForever {
            Truth.assertThat(it.data).isNull()
            Truth.assertThat(it.message).isEqualTo("Failed")
        }
    }

    @Test
    fun `test getFarmerId call when name exists`() = runTest {
        subject.loadSellers()
        Truth.assertThat(subject.getSellerId("Rajesh")).isEqualTo("S1234")
        Truth.assertThat(subject.factor.value).isEqualTo(SellerPageViewModel.LOYALTY_FACTOR)
    }

    @Test
    fun `test getFarmerId call when name does not exist`() = runTest {
        Truth.assertThat(subject.getSellerId("VIKAS")).isEqualTo("")
        Truth.assertThat(subject.factor.value).isEqualTo(SellerPageViewModel.NO_LOYALTY_FACTOR)
    }

    @Test
    fun `test getFarmerName call when ID exists`() = runTest {
        subject.loadSellers()
        Truth.assertThat(subject.getSellerName("S5424")).isEqualTo("SURESH")
        Truth.assertThat(subject.factor.value).isEqualTo(SellerPageViewModel.LOYALTY_FACTOR)
    }

    @Test
    fun `test getFarmerName call when ID does not exist`() = runTest {
        Truth.assertThat(subject.getSellerName("S4232")).isEqualTo("")
        Truth.assertThat(subject.factor.value).isEqualTo(SellerPageViewModel.NO_LOYALTY_FACTOR)
    }

    @Test
    fun `test updatePricePerKg call, farmer info doesn't exist`() = runTest {
        subject.updateWeight(24.1)
        subject.updatePricePerKg(102.12)
        Truth.assertThat(subject.grossPrice.value)
            .isEqualTo(useCase.computeGrossPrice(24.1, 102.12, SellerPageViewModel.NO_LOYALTY_FACTOR))
    }

    @Test
    fun `test updatePricePerKg call, farmer info exists`() = runTest {
        subject.loadSellers()
        subject.getSellerId("Suresh")
        subject.updateWeight(24.1)
        subject.updatePricePerKg(102.12)
        Truth.assertThat(subject.grossPrice.value)
            .isEqualTo(useCase.computeGrossPrice(24.1, 102.12, SellerPageViewModel.LOYALTY_FACTOR))
    }

    @Test
    fun `test updateWeight call, farmer info doesn't exist`() = runTest {
        subject.updatePricePerKg(123.12)
        subject.updateWeight(24.1)
        Truth.assertThat(subject.grossPrice.value)
            .isEqualTo(useCase.computeGrossPrice(24.1, 123.12, SellerPageViewModel.NO_LOYALTY_FACTOR))
    }

    @Test
    fun `test updateWeight call, farmer info exists`() = runTest {
        subject.loadSellers()
        subject.getSellerName("S5424")
        subject.updatePricePerKg(123.12)
        subject.updateWeight(24.1)
        Truth.assertThat(subject.grossPrice.value)
            .isEqualTo(useCase.computeGrossPrice(24.1, 123.12, SellerPageViewModel.LOYALTY_FACTOR))
    }
}