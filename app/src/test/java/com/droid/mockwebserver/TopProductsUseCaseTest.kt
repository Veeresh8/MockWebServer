package com.droid.mockwebserver

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit

@OptIn(ExperimentalCoroutinesApi::class)
class TopProductsUseCaseTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var productService: ProductService
    private lateinit var productsUseCase: TopProductsUseCase

    private val client = OkHttpClient.Builder().build()
    private val contentType = "application/json".toMediaType()

    @Before
    fun setup() {
        val jsonConverter = Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        }

        mockWebServer = MockWebServer()

        productService = Retrofit.Builder().baseUrl(mockWebServer.url("/"))
            .client(client).addConverterFactory(jsonConverter.asConverterFactory(contentType))
            .build().create(ProductService::class.java)

        productsUseCase = TopProductsUseCase(productService)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `check retry logic`() = runTest {
        val response = MockResponse()
            .setBody("Bad Request")
            .setResponseCode(400)

        mockWebServer.enqueue(response)

        val flow = productsUseCase.getTopSubProducts()
        launch {
            flow.collect {
                assertTrue(it is Result.Success)
            }
        }

        advanceTimeBy(3000)
        mockWebServer.enqueue(response)

        val fakeResponse = Fake.buildProducts(15)
        val successResponse = MockResponse().setBody(fakeResponse.first).setResponseCode(200)

        advanceTimeBy(3000)
        mockWebServer.enqueue(successResponse)
    }

    @Test
    fun `check if 400 is thrown`() = runTest {
        val response = MockResponse()
            .setBody("The client messed this up")
            .setResponseCode(400)

        mockWebServer.enqueue(response)

        val result = productsUseCase.getTopProducts().single()
        assertTrue((result as Result.Error).exception is HttpException)
    }

    @Test
    fun `check if 500 is thrown`() = runTest {
        val response = MockResponse()
            .setBody("Server messed this up!")
            .setResponseCode(500)

        mockWebServer.enqueue(response)

        val flow = productsUseCase.getTopProducts()
        launch {
            flow.collect {
                assertTrue((it as Result.Error).exception is HttpException)
            }
        }
    }
//
    @Test
    fun `check if exception is thrown for malformed JSON`() = runTest {
        val response = MockResponse().setBody("Malformed JSON").setResponseCode(200)

        mockWebServer.enqueue(response)

        val flow = productsUseCase.getTopProducts()
        launch {
            flow.collect {
                assertTrue(it is Result.Error)
                (it as Result.Error).exception.message?.contains("Malformed JSON")
            }
        }
    }
//
    @Test
    fun `check successful response`() = runTest {
        val fakeResponse = Fake.buildProducts(15)

        val response = MockResponse()
            .setBody(fakeResponse.first)
            .setResponseCode(200)

        mockWebServer.enqueue(response)

        val flow = productsUseCase.getTopProducts()
        launch {
            flow.collect {
                assertTrue(it is Result.Success)
                assertTrue((it as Result.Success).data == fakeResponse.second)
            }
        }
    }
}