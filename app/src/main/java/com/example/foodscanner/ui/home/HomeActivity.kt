package com.example.foodscanner.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.example.foodscanner.data.auth.FirebaseAuthManager
import com.example.foodscanner.data.database.FoodScannerDatabase
import com.example.foodscanner.data.repository.ScanHistoryRepository
import com.example.foodscanner.databinding.ActivityHomeBinding
import com.example.foodscanner.ui.adapter.ScanHistoryAdapter
import com.example.foodscanner.ui.auth.LoginActivity
import com.example.foodscanner.ui.history.ScanHistoryActivity
import com.example.foodscanner.ui.product.ProductDetailsActivity
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val authManager = FirebaseAuthManager()
    private lateinit var scanHistoryRepository: ScanHistoryRepository
    private lateinit var adapter: ScanHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize repository
        val database = FoodScannerDatabase.getDatabase(this)
        scanHistoryRepository = ScanHistoryRepository(database.scanHistoryDao())

        // Check authentication
        if (!authManager.isLoggedIn()) {
            navigateToLogin()
            return
        }

        setupUI()
        setupRecyclerView()
        loadRecentScans()
        setupClickListeners()
    }

    private fun setupUI() {
        val userEmail = authManager.getUserEmail()
        val userName = authManager.getUserName()
        binding.welcomeMessage.text = "Welcome, ${userName.takeIf { it.isNotEmpty() } ?: userEmail}"
    }

    private fun setupRecyclerView() {
        adapter = ScanHistoryAdapter { scan ->
            val intent = Intent(this, ProductDetailsActivity::class.java).apply {
                putExtra("barcode", scan.barcode)
                putExtra("from_history", true)
            }
            startActivity(intent)
        }
        binding.recentScansRecycler.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = this@HomeActivity.adapter
        }
    }

    private fun loadRecentScans() {
        val userId = authManager.getUserId()
        lifecycleScope.launch {
            val recentScans = scanHistoryRepository.getRecentScans(userId, 5)
            if (recentScans.isEmpty()) {
                binding.noScansMessage.visibility = View.VISIBLE
                binding.recentScansRecycler.visibility = View.GONE
                binding.viewHistoryButton.visibility = View.GONE
            } else {
                binding.noScansMessage.visibility = View.GONE
                binding.recentScansRecycler.visibility = View.VISIBLE
                binding.viewHistoryButton.visibility = View.VISIBLE
                adapter.submitList(recentScans)
            }
        }
    }

    private fun setupClickListeners() {
        binding.scanButton.setOnClickListener {
            startBarcodeScan()
        }

        binding.viewHistoryButton.setOnClickListener {
            startActivity(Intent(this, ScanHistoryActivity::class.java))
        }

        binding.logoutButton.setOnClickListener {
            authManager.logout()
            navigateToLogin()
        }
    }

    private fun startBarcodeScan() {
        GmsBarcodeScanning.getClient(this)
            .startScan()
            .addOnSuccessListener { barcode: Barcode ->
                val barcodeValue = barcode.rawValue
                if (!barcodeValue.isNullOrEmpty()) {
                    val intent = Intent(this, ProductDetailsActivity::class.java).apply {
                        putExtra("barcode", barcodeValue)
                    }
                    startActivity(intent)
                }
            }
            .addOnCanceledListener {
                // Scanner cancelled
            }
            .addOnFailureListener { e: Exception ->
                // Handle scanner error
            }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        // Refresh recent scans when activity resumes
        loadRecentScans()
    }
}
