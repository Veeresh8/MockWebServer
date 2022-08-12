package com.droid.mockwebserver

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class TopProductsUseCase @Inject constructor(private val productService: ProductService) {
    fun getTopProducts(): Flow<Result<ProductsResponse>> {

        return flow<Result<ProductsResponse>> {
            val productResponse = productService.fetchProducts()

            emit(Result.Success(productResponse))
        }.catch {
            emit(Result.Error(it))
        }
    }

    fun getTopSubProducts(): Flow<Result<ProductsResponse>> {

        return flow<Result<ProductsResponse>> {
            val productResponse = productService.fetchProducts()

            emit(Result.Success(productResponse))
        }.retry(2) {
            if (it is java.lang.Exception) {
                delay(3000)
            }
            return@retry true
        }.catch {
            emit(Result.Error(it))
        }
    }
}