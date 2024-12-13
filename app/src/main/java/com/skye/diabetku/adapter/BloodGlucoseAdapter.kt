package com.skye.diabetku.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skye.diabetku.data.remote.response.DataItem
import com.skye.diabetku.databinding.ItemRowHistoryBinding
import com.skye.diabetku.view.EditGlucoseDataActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BloodGlucoseAdapter(
    private val onItemClick: (DataItem) -> Unit
):ListAdapter<DataItem, BloodGlucoseAdapter.MyViewHolder>(
    DIFF_CALLBACK
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemRowHistoryBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val glucoseData = getItem(position)
        holder.bind(glucoseData)
        holder.itemView.setOnClickListener {
            onItemClick(glucoseData)
        }
    }

    inner class MyViewHolder(private val binding: ItemRowHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(glucose: DataItem) {
            val date = glucose.formattedDate?: ""
            val time = glucose.formattedTime?: ""

            val dateTimeString = formatDateTime(date, time)

            binding.tvTanggalWaktu.text = dateTimeString
            binding.tvGlukosa.text = glucose.glucoseValue ?: ""
            binding.labelJenis.text = glucose.testType ?: ""

        }
        }
        private fun formatDateTime(date: String, time: String): String {
            try {

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val dateObj = dateFormat.parse(date)

                val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                val day = dayFormat.format(dateObj ?: Date())


                return "$day, $date - $time"
            } catch (e: Exception) {
                e.printStackTrace()
                return "$date - $time"
            }
        }

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataItem>() {

            override fun areItemsTheSame(
                oldItem: DataItem,
                newItem: DataItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: DataItem,
                newItem: DataItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
