package com.droid.mockwebserver

@kotlinx.serialization.Serializable
data class ProductsResponse(
    val products: List<Product>
) {
    @kotlinx.serialization.Serializable
    data class Product(
        val id: Long,
        val title: String?,
        val description: String?,
        val price: Long
    )
}


