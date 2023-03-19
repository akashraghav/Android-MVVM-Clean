package com.raghav.market.domain

import android.util.Log
import com.google.common.truth.Truth.assertThat
import com.raghav.market.domain.model.CityInfo
import com.raghav.market.domain.model.Result
import com.raghav.market.domain.model.SellerInfo
import com.raghav.market.domain.repository.SellerDetailsRepository
import com.raghav.market.domain.usecase.SellerUseCase
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import java.math.RoundingMode
import java.net.UnknownHostException
import java.text.DecimalFormat
import kotlin.math.absoluteValue

class SellerUseCaseTest {

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.v(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.w(any(), any<String>()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
    }

    @Test
    fun `test seller api call use case success`() = runBlocking {
        val repository = mockk<SellerDetailsRepository>()
        val sellerList: List<SellerInfo> = ArrayList()
        coEvery { repository.getSellersInfo() }.returns(Result.Success(sellerList))

        val subject = SellerUseCase(repository)
        assertThat(subject.getSellerInfoList().data).isEqualTo(sellerList)
        coVerify(exactly = 1) { repository.getSellersInfo() }
    }

    @Test
    fun `test seller api call use case on failed result, expectation - retries call`(): Unit = runBlocking {
        val repository = mockk<SellerDetailsRepository>()
        coEvery { repository.getSellersInfo() }.returns(Result.Error("Failed", 500))
        val subject = SellerUseCase(repository)
        assertThat(subject.getSellerInfoList().data).isNull()
        coVerify(exactly = 3) { repository.getSellersInfo() }
    }

    @Test
    fun `test seller api call use case on exception, expectation - retries call`(): Unit = runBlocking {
        val repository = mockk<SellerDetailsRepository>()
        coEvery { repository.getSellersInfo() }.throws(UnknownHostException("Failed"))
        assertThrows<UnknownHostException> {
            val subject = SellerUseCase(repository)
            assertThat(subject.getSellerInfoList().data).isNull()
        }
        coVerify(exactly = 3) { repository.getSellersInfo() }
    }

    @Test
    fun `test cities api call use case success`() = runBlocking {
        val repository = mockk<SellerDetailsRepository>()
        val sellerList: List<CityInfo> = ArrayList()
        coEvery { repository.getCitiesInfo() }.returns(Result.Success(sellerList))

        val subject = SellerUseCase(repository)
        assertThat(subject.getCityInfoList().data).isEqualTo(sellerList)
        coVerify(exactly = 1) { repository.getCitiesInfo() }
    }

    @Test
    fun `test cities api call use case on failed result, expectation - retries call`(): Unit = runBlocking {
        val repository = mockk<SellerDetailsRepository>()
        coEvery { repository.getCitiesInfo() }.returns(Result.Error("Failed", 500))
        val subject = SellerUseCase(repository)
        assertThat(subject.getCityInfoList().data).isNull()
        coVerify(exactly = 3) { repository.getCitiesInfo() }
    }

    @Test
    fun `test cities api call use case on exception, expectation - retries call`(): Unit = runBlocking {
        val repository = mockk<SellerDetailsRepository>()
        coEvery { repository.getCitiesInfo() }.throws(UnknownHostException("Failed"))
        assertThrows<UnknownHostException> {
            val subject = SellerUseCase(repository)
            assertThat(subject.getCityInfoList().data).isNull()
            coVerify(exactly = 3) { repository.getCitiesInfo() }
        }
    }

    @Test
    fun `test calculate gross price with valid values, expectation - correct result`() {
        val repository = mockk<SellerDetailsRepository>()
        val subject = SellerUseCase(repository)

        val weight = 12.4412123
        val pricePerKg = 108.9121
        val factor = 1.12
        val formatter = DecimalFormat("#.00")
        formatter.roundingMode = RoundingMode.DOWN
        val expected = formatter.format(weight * pricePerKg * factor).toDouble()

        val result = subject.computeGrossPrice(weight, pricePerKg, factor)
        assertThat(result).isEqualTo(expected)
    }

    @Test // computeGrossPrice function is setup to always return absolute value
    fun `test calculate gross price with invalid values, expectation - correct result`() {
        val repository = mockk<SellerDetailsRepository>()
        val subject = SellerUseCase(repository)

        val weight = -12.4412123
        val pricePerKg = -108.9121
        val factor = -1.12
        val formatter = DecimalFormat("#.00")
        formatter.roundingMode = RoundingMode.DOWN
        val expected = formatter.format(weight * pricePerKg * factor).toDouble().absoluteValue

        val result = subject.computeGrossPrice(weight, pricePerKg, factor)
        assertThat(result).isEqualTo(expected)
    }
}