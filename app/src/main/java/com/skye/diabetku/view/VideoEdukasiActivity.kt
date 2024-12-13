package com.skye.diabetku.view

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.skye.diabetku.data.Result
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.skye.diabetku.R
import com.skye.diabetku.adapter.ArticleAdapter
import com.skye.diabetku.adapter.VideoAdapter
import com.skye.diabetku.databinding.ActivityArtikelBinding
import com.skye.diabetku.databinding.ActivityVideoEdukasiBinding
import com.skye.diabetku.viewmodel.HomeViewModel
import com.skye.diabetku.viewmodel.ViewModelFactory

class VideoEdukasiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoEdukasiBinding
    private lateinit var videoAdapter: VideoAdapter
    private val homeViewModel: HomeViewModel by viewModels(){
        ViewModelFactory.getInstance(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoEdukasiBinding.inflate(layoutInflater)
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
        homeViewModel.getVideoData()
    }
    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvVideo.layoutManager = layoutManager
        videoAdapter = VideoAdapter(isHorizontal = false)
        binding.rvVideo.adapter = videoAdapter
    }

    private fun observeViewModel() {
        homeViewModel.videoData.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    val videoList = result.data.filterNotNull()
                    videoAdapter.submitList(videoList)
                    binding.progressBar.visibility = View.GONE
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
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