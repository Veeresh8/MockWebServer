package com.droid.mockwebserver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val productsViewModel: ProductsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            productsViewModel.fetchTopProducts()

            repeatOnLifecycle(Lifecycle.State.CREATED) {
                productsViewModel.uiState.collect {
                    when (it) {
                        is DashboardState.Error -> {
                            Log.d("MainActivity", "Error: ${it.error}")
                        }
                        is DashboardState.Loading -> {
                            Log.d("MainActivity", "Loading: ${it.loadingMessage}")
                        }
                        is DashboardState.Success -> {
                            Log.d("MainActivity", "Success: ${it.products.size}")
                        }
                    }
                }
            }
        }
    }
}