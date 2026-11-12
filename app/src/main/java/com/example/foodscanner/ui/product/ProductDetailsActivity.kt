package com.example.foodscanner.ui.product

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.foodscanner.R
import com.example.foodscanner.data.api.model.Product
import com.example.foodscanner.data.auth.FirebaseAuthManager
import com.example.foodscanner.data.database.FoodScannerDatabase
import com.example.foodscanner.data.entity.ScanHistory
import com.example.foodscanner.data.repository.ProductRepository
import com.example.foodscanner.data.repository.ScanHistoryRepository
import com.example.foodscanner.databinding.ActivityProductDetailsBinding
import kotlinx.coroutines.launch

class ProductDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailsBinding
    private val productRepository = ProductRepository()
    private lateinit var scanHistoryRepository: ScanHistoryRepository
    private val authManager = FirebaseAuthManager()
    private var currentBarcode: String? = null
    private var fromHistory: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize repository
        val database = FoodScannerDatabase.getDatabase(this)
        scanHistoryRepository = ScanHistoryRepository(database.scanHistoryDao())

        setupClickListeners()

        currentBarcode = intent.getStringExtra("barcode")
        fromHistory = intent.getBooleanExtra("from_history", false)

        if (!currentBarcode.isNullOrEmpty()) {
            fetchProductDetails(currentBarcode!!)
        }
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.tryAgainButton.setOnClickListener {
            if (!currentBarcode.isNullOrEmpty()) {
                fetchProductDetails(currentBarcode!!)
            }
        }
    }

    private fun fetchProductDetails(barcode: String) {
        showLoading(true)
        hideError()
        hideProductDetails()

        lifecycleScope.launch {
            val result = productRepository.getProductDetails(barcode)

            result.onSuccess { product ->
                if (product != null) {
                    displayProductDetails(product, barcode)
                    if (!fromHistory) {
                        saveToHistory(product, barcode)
                    }
                } else {
                    showError("Product not found")
                }
            }

            result.onFailure { exception ->
                showError(exception.message ?: "Failed to fetch product details")
            }

            showLoading(false)
        }
    }

    private fun displayProductDetails(product: Product, barcode: String) {
        binding.apply {
            showProductDetails()

            // Product Image
            if (!product.imageUrl.isNullOrEmpty()) {
                Glide.with(this@ProductDetailsActivity)
                    .load(product.imageUrl)
                    .placeholder(R.color.gray_200)
                    .error(R.color.gray_200)
                    .into(productImage)
            }

            // Product Name
            productName.text = product.productName ?: "Unknown Product"

            // Brand
            if (!product.brands.isNullOrEmpty()) {
                brand.text = product.brands
                brandContainer.visibility = View.VISIBLE
            } else {
                brandContainer.visibility = View.GONE
            }

            // Barcode
            this.barcode.text = barcode
            barcodeContainer.visibility = View.VISIBLE

            // Quantity
            if (!product.quantity.isNullOrEmpty()) {
                quantity.text = product.quantity
                quantityContainer.visibility = View.VISIBLE
            } else {
                quantityContainer.visibility = View.GONE
            }

            // Nutri-Score
            if (!product.nutriscoreGrade.isNullOrEmpty()) {
                val grade = product.nutriscoreGrade!!.uppercase()
                nutriScore.text = grade
                nutriScore.setBackgroundColor(
                    when (grade) {
                        "A" -> getColor(android.R.color.holo_green_dark)
                        "B" -> getColor(android.R.color.holo_green_light)
                        "C" -> getColor(android.R.color.holo_orange_light)
                        "D" -> getColor(android.R.color.holo_orange_dark)
                        "E" -> getColor(android.R.color.holo_red_light)
                        else -> getColor(android.R.color.darker_gray)
                    }
                )
                nutriScoreContainer.visibility = View.VISIBLE
            } else {
                nutriScoreContainer.visibility = View.GONE
            }
        }
    }

    private fun saveToHistory(product: Product, barcode: String) {
        lifecycleScope.launch {
            val scan = ScanHistory(
                userId = authManager.getUserId(),
                barcode = barcode,
                productName = product.productName,
                brand = product.brands,
                imageUrl = product.imageUrl,
                nutriScore = product.nutriscoreGrade,
                quantity = product.quantity,
                scanDate = System.currentTimeMillis()
            )
            scanHistoryRepository.insertScan(scan)
        }
    }

    private fun showProductDetails() {
        binding.apply {
            productImage.visibility = View.VISIBLE
            productName.visibility = View.VISIBLE
        }
    }

    private fun hideProductDetails() {
        binding.apply {
            productImage.visibility = View.GONE
            productName.visibility = View.GONE
            brandContainer.visibility = View.GONE
            barcodeContainer.visibility = View.GONE
            quantityContainer.visibility = View.GONE
            nutriScoreContainer.visibility = View.GONE
        }
    }

    private fun showLoading(show: Boolean) {
        binding.loadingProgress.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        binding.apply {
            errorMessage.text = message
            errorMessage.visibility = View.VISIBLE
            tryAgainButton.visibility = View.VISIBLE
        }
    }

    private fun hideError() {
        binding.apply {
            errorMessage.visibility = View.GONE
            tryAgainButton.visibility = View.GONE
        }
    }
}
