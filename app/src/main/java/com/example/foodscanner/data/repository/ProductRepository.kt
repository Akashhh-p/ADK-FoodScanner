package com.example.foodscanner.data.repository

import com.example.foodscanner.data.api.RetrofitClient
import com.example.foodscanner.data.api.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductRepository {

    suspend fun getProductDetails(barcode: String): Result<Product?> = withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.openFoodFactsService.getProduct(barcode)
            if (response.product != null) {
                Result.success(response.product)
            } else {
                Result.failure(Exception("Product not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
