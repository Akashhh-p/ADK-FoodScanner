package com.example.foodscanner.data.api.model

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    @SerializedName("product")
    val product: Product?
)

data class Product(
    @SerializedName("product_name")
    val productName: String?,
    @SerializedName("brands")
    val brands: String?,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("nutriscore_grade")
    val nutriscoreGrade: String?,
    @SerializedName("quantity")
    val quantity: String?
)
