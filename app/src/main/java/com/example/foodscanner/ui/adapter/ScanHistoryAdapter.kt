package com.example.foodscanner.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodscanner.R
import com.example.foodscanner.data.entity.ScanHistory
import com.example.foodscanner.databinding.ItemScanHistoryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScanHistoryAdapter(private val onItemClick: (ScanHistory) -> Unit) :
    RecyclerView.Adapter<ScanHistoryAdapter.ScanHistoryViewHolder>() {

    private val scans = mutableListOf<ScanHistory>()

    fun submitList(newScans: List<ScanHistory>) {
        scans.clear()
        scans.addAll(newScans)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScanHistoryViewHolder {
        val binding = ItemScanHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScanHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScanHistoryViewHolder, position: Int) {
        val scan = scans[position]
        holder.bind(scan)
        holder.itemView.setOnClickListener {
            onItemClick(scan)
        }
    }

    override fun getItemCount() = scans.size

    class ScanHistoryViewHolder(private val binding: ItemScanHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(scan: ScanHistory) {
            binding.apply {
                // Load image
                if (!scan.imageUrl.isNullOrEmpty()) {
                    Glide.with(productImage.context)
                        .load(scan.imageUrl)
                        .placeholder(R.color.gray_200)
                        .error(R.color.gray_200)
                        .into(productImage)
                }

                // Product details
                productName.text = scan.productName ?: "Unknown Product"
                brand.text = scan.brand?.takeIf { it.isNotEmpty() } ?: "Unknown Brand"
                barcode.text = "Barcode: ${scan.barcode}"

                // Format date
                val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                val date = Date(scan.scanDate)
                scanDate.text = "Scanned on ${sdf.format(date)}"

                // Nutri-Score
                if (!scan.nutriScore.isNullOrEmpty()) {
                    nutriScore.visibility = android.view.View.VISIBLE
                    nutriScore.text = "Score: ${scan.nutriScore.uppercase()}"
                    nutriScore.setBackgroundColor(
                        when (scan.nutriScore?.uppercase()) {
                            "A" -> itemView.context.getColor(android.R.color.holo_green_dark)
                            "B" -> itemView.context.getColor(android.R.color.holo_green_light)
                            "C" -> itemView.context.getColor(android.R.color.holo_orange_light)
                            "D" -> itemView.context.getColor(android.R.color.holo_orange_dark)
                            "E" -> itemView.context.getColor(android.R.color.holo_red_light)
                            else -> itemView.context.getColor(android.R.color.darker_gray)
                        }
                    )
                } else {
                    nutriScore.visibility = android.view.View.GONE
                }
            }
        }
    }
}
