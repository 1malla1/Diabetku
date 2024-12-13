package com.skye.diabetku.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skye.diabetku.R
import com.skye.diabetku.data.model.FoodItem
import com.skye.diabetku.data.remote.response.DataItem
import com.skye.diabetku.databinding.ItemRowFoodRecommendationBinding
import com.skye.diabetku.databinding.ItemRowHistoryBinding
import com.skye.diabetku.view.EditGlucoseDataActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FoodAdapter: RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {
    private var foods = listOf<FoodItem>()

    class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.tvNamaMakanan)
        val carbsTextView: TextView = itemView.findViewById(R.id.tvKarbohidrat)
        val caloriesTextView: TextView = itemView.findViewById(R.id.tvKalori)
        val glucoseTextView: TextView = itemView.findViewById(R.id.tvGlukosa)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row_food_recommendation, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val food = foods[position]
        holder.nameTextView.text = food.name
        holder.carbsTextView.text = "${food.carbohydrate}g"
        holder.caloriesTextView.text = "${food.calories} kcal"
        holder.glucoseTextView.text = "${food.estimatedGlucose} mg/dL"
    }

    override fun getItemCount() = foods.size

    fun submitList(newFoods: List<FoodItem>) {
        foods = newFoods
        notifyDataSetChanged()
    }
}
