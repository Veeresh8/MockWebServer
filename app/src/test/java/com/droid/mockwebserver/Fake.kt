package com.droid.mockwebserver

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object Fake {

    fun buildProducts(count: Int): Pair<String, ProductsResponse> {
        val products = arrayListOf<ProductsResponse.Product>()

        repeat(count) {
            val product = ProductsResponse.Product(
                id = (1..999).random().toLong(),
                title = "Product title: $it",
                description = "Description for: $it",
                price = (500..999).random().toLong()
            )
            products.add(product)
        }

        val productsResponse = ProductsResponse(products)
        return Pair(Json.encodeToString(productsResponse), productsResponse)
    }
}