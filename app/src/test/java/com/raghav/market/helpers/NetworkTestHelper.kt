package com.raghav.market.helpers

import com.raghav.market.data.remote.RetrofitCreator
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import java.io.File
import java.util.concurrent.TimeUnit

internal object NetworkTestHelper {

    internal fun getRetrofit(server: MockWebServer): Retrofit {
        return RetrofitCreator(server.url("/").toString()).getInstance()
    }

    internal fun MockWebServer.queueResponseFile(fileName: String, responseCode: Int = 200, delay: Long = 0) {
        enqueue(
            MockResponse()
                .setResponseCode(responseCode)
                .setBodyDelay(delay, TimeUnit.MILLISECONDS)
                .setBody(readFileToString(fileName))
        )
    }

    internal fun readFileToString(fileName: String): String {
        val uri = ClassLoader.getSystemClassLoader().getResource(fileName).toURI()
        val file = File(uri.path)
        return String(file.readBytes())
    }
}