package com.droid.mockwebserver
import retrofit2.http.GET

interface ProductService {

    @GET("products")
    suspend fun fetchProducts(): ProductsResponse
}