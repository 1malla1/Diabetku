package com.skye.diabetku.view

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputLayout
import com.skye.diabetku.R
import com.skye.diabetku.adapter.FoodAdapter
import com.skye.diabetku.databinding.ActivityFoodRecommendationBinding
import com.skye.diabetku.viewmodel.FoodRecommendationViewModel

data class FoodItem(val name: String, val calories: Int)

class FoodRecommendationActivity : AppCompatActivity() {
    private val foodRecommendationViewModel: FoodRecommendationViewModel by viewModels()

    private lateinit var binding: ActivityFoodRecommendationBinding
    private lateinit var foodAdapter: FoodAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodRecommendationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar()
        setRecyclerView()
        setupObservers()
        binding.progressBar.visibility = View.GONE

        binding.btnCek.setOnClickListener {
            val glucoseLevel = binding.edtGlucose.text.toString().toIntOrNull()
            if (glucoseLevel != null) {
                clearInputError()
                hideKeyboard()
                showLoadingIndicator(true)
                foodRecommendationViewModel.getFoodRecommendations(glucoseLevel)
            } else {
                setInputError("Invalid Glocose Level")
            }
        }
    }

    private fun setupObservers() {
        foodRecommendationViewModel.foodRecommendations.observe(this) { foodList ->
            showLoadingIndicator(false)
            binding.btnCek.isEnabled = true
            binding.btnCek.text = getString(R.string.cek_sekarang)
            if (foodList.isNotEmpty()) {
                foodAdapter.submitList(foodList)
            } else {
                showToast("No Recommendation funds")
            }
        }

        foodRecommendationViewModel.isLoading.observe(this) { isLoading ->
            showLoadingIndicator(isLoading)
            binding.btnCek.isEnabled = !isLoading
            binding.btnCek.text = if (isLoading) getString(R.string.loading) else getString(R.string.cek_sekarang)
        }

        foodRecommendationViewModel.error.observe(this) { errorMessage ->
            showLoadingIndicator(false)
            binding.btnCek.isEnabled = true
            binding.btnCek.text = getString(R.string.cek_rekomendasi)
            showToast(errorMessage)
        }
    }

    private fun showLoadingIndicator(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun setRecyclerView() {
        foodAdapter = FoodAdapter()
        binding.rvRekomendasi.apply {
            adapter = foodAdapter
            layoutManager = LinearLayoutManager(this@FoodRecommendationActivity)
        }
    }

    private fun setToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        val drawable: Drawable? = toolbar.navigationIcon
        drawable?.let {
            DrawableCompat.setTint(it, getColor(R.color.white))
            toolbar.navigationIcon = it
        }
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.edtGlucose.windowToken, 0)
    }

    private fun setInputError(message: String) {
        val inputLayout: TextInputLayout = binding.edtGlucoseLayout
        inputLayout.error = message
        inputLayout.requestFocus()
    }

    private fun clearInputError() {
        val inputLayout: TextInputLayout = binding.edtGlucoseLayout
        inputLayout.error = null
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}