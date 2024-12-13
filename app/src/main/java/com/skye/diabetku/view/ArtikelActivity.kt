package com.skye.diabetku.view

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.skye.diabetku.R
import com.skye.diabetku.data.remote.retrofit.ArticleApiConfig
import com.skye.diabetku.databinding.ActivityArtikelBinding
import com.skye.diabetku.adapter.ArticleAdapter
import com.skye.diabetku.viewmodel.HomeViewModel
import com.skye.diabetku.viewmodel.ViewModelFactory

class ArtikelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArtikelBinding
    private lateinit var articleAdapter: ArticleAdapter
    private val homeViewModel: HomeViewModel by viewModels(){
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArtikelBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        setupRecyclerView()
        observeViewModel()

        if (homeViewModel.articleData.value == null) {
            homeViewModel.getArticle("diabetes", "health", "id", ArticleApiConfig.API_KEY)
        }

    }
    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvArtikel.layoutManager = layoutManager
        articleAdapter = ArticleAdapter(isHorizontal = false)
        binding.rvArtikel.adapter = articleAdapter
    }

    private fun observeViewModel() {
        homeViewModel.articleData.observe(this) { articleResponse ->
            articleResponse?.let {
                articleAdapter.submitList(it.articles)
                binding.progressBar.visibility = View.GONE
            } ?: run {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this, "Fetching data error", Toast.LENGTH_SHORT).show()
            }
        }

        homeViewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}