package com.droid.mockwebserver

sealed class DashboardState {
    data class Success(val products: List<ProductsResponse.Product>) : DashboardState()
    data class Error(val error: String) : DashboardState()
    data class Loading(val loadingMessage: String) : DashboardState()
}

