package com.raghav.market.data

import com.google.common.truth.Truth.assertThat
import com.google.gson.stream.MalformedJsonException
import com.raghav.market.data.remote.api.SellerDetailsApi
import com.raghav.market.data.repoimpl.SellerDetailsRepositoryImpl
import com.raghav.market.domain.repository.SellerDetailsRepository
import com.raghav.market.helpers.NetworkTestHelper.getRetrofit
import com.raghav.market.helpers.NetworkTestHelper.queueResponseFile
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertThrows

class MandiApiTest {

    private val server: MockWebServer = MockWebServer()
    private val subject: SellerDetailsRepository by lazy {
        val api = getRetrofit(server).create(SellerDetailsApi::class.java)
        SellerDetailsRepositoryImpl(api)
    }

    @Before
    fun setup() {
        server.start()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `test sellers api call on success`() = runBlocking {
        server.queueResponseFile("sellers.json")
        val response = subject.getSellersInfo()
        assertThat(response.data).isNotNull()
        assertThat(response.data!!.size).isEqualTo(6)
    }

    @Test
    fun `test sellers api call on failure error code`() = runBlocking {
        server.queueResponseFile("sellers.json", responseCode = 404)
        val response = subject.getSellersInfo()
        assertThat(response.data).isNull()
        assertThat(response.message).isNotEmpty()
        assertThat(response.errorCode).isEqualTo(404)
    }

    @Test
    fun `test cities api call on success`() = runBlocking {
        server.queueResponseFile("cities.json")
        val response = subject.getCitiesInfo()
        assertThat(response.data).isNotNull()
        assertThat(response.data!!.size).isEqualTo(9)
    }

    @Test
    fun `test cities api call on failure error code`() = runBlocking {
        server.queueResponseFile("cities.json", responseCode = 404)
        val response = subject.getCitiesInfo()
        assertThat(response.data).isNull()
        assertThat(response.message).isNotEmpty()
        assertThat(response.errorCode).isEqualTo(404)
    }

    @Test
    fun `test sellers api call on invalid json`(): Unit = runBlocking {
        server.queueResponseFile("invalid.json")
        assertThrows<MalformedJsonException> { subject.getSellersInfo() }
    }

    @Test
    fun `test citys api call on invalid json`(): Unit = runBlocking {
        server.queueResponseFile("invalid.json")
        assertThrows<MalformedJsonException> { subject.getCitiesInfo() }
    }
}