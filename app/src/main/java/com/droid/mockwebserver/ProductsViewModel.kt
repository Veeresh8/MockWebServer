package com.droid.mockwebserver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(private val topProductsUseCase: TopProductsUseCase) :
    ViewModel() {

    val uiState = MutableStateFlow<DashboardState>(DashboardState.Loading(""))

    fun fetchTopProducts() {
        uiState.value = DashboardState.Loading("Fetching Products")

        viewModelScope.launch {
            //topProductsUseCase.getTopSubProducts()

            topProductsUseCase.getTopProducts().collect {
                when (it) {
                    is Result.Error -> {
                        uiState.value = DashboardState.Error(it.exception.message.toString())
                    }
                    is Result.Success -> {
                        uiState.value = DashboardState.Success(it.data.products)
                    }
                }
            }
        }
    }
}
