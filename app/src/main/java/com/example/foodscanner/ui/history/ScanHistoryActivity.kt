package com.example.foodscanner.ui.history

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodscanner.data.auth.FirebaseAuthManager
import com.example.foodscanner.data.database.FoodScannerDatabase
import com.example.foodscanner.data.repository.ScanHistoryRepository
import com.example.foodscanner.databinding.ActivityScanHistoryBinding
import com.example.foodscanner.ui.adapter.ScanHistoryAdapter
import com.example.foodscanner.ui.product.ProductDetailsActivity
import kotlinx.coroutines.launch

class ScanHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityScanHistoryBinding
    private lateinit var scanHistoryRepository: ScanHistoryRepository
    private val authManager = FirebaseAuthManager()
    private lateinit var adapter: ScanHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize repository
        val database = FoodScannerDatabase.getDatabase(this)
        scanHistoryRepository = ScanHistoryRepository(database.scanHistoryDao())

        setupRecyclerView()
        loadScanHistory()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        adapter = ScanHistoryAdapter { scan ->
            val intent = Intent(this, ProductDetailsActivity::class.java).apply {
                putExtra("barcode", scan.barcode)
                putExtra("from_history", true)
            }
            startActivity(intent)
        }
        binding.scanHistoryRecycler.apply {
            layoutManager = LinearLayoutManager(this@ScanHistoryActivity)
            adapter = this@ScanHistoryActivity.adapter
        }
    }

    private fun loadScanHistory() {
        val userId = authManager.getUserId()
        lifecycleScope.launch {
            scanHistoryRepository.getUserScans(userId).collect { scans ->
                if (scans.isEmpty()) {
                    binding.noScansMessage.visibility = View.VISIBLE
                    binding.scanHistoryRecycler.visibility = View.GONE
                } else {
                    binding.noScansMessage.visibility = View.GONE
                    binding.scanHistoryRecycler.visibility = View.VISIBLE
                    adapter.submitList(scans)
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }
}
