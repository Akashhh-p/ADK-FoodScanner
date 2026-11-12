package com.example.foodscanner.data.api

import com.example.foodscanner.data.api.model.ProductResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface OpenFoodFactsService {

    @GET("api/v2/product/{barcode}.json?fields=product_name,brands,image_url,nutriscore_grade,quantity")
    suspend fun getProduct(@Path("barcode") barcode: String): ProductResponse
}
