package com.skye.diabetku.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.skye.diabetku.R
import com.skye.diabetku.data.remote.retrofit.ArticleApiConfig
import com.skye.diabetku.databinding.FragmentHomeBinding
import com.skye.diabetku.adapter.ArticleAdapter
import com.skye.diabetku.adapter.VideoAdapter
import com.skye.diabetku.data.Result
import com.skye.diabetku.data.remote.response.GetData
import com.skye.diabetku.data.remote.response.RegisterData
import com.skye.diabetku.itemdecoration.HorizontalItemDecoration
import com.skye.diabetku.viewmodel.HomeViewModel
import com.skye.diabetku.viewmodel.ViewModelFactory


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var videoAdapter: VideoAdapter
    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.getUserData()
        homeViewModel.getDiabetesCheckData()
        homeViewModel.getVideoData()
        setRecyclerView()
        observeViewModel()

        if (homeViewModel.articleData.value == null) {
            homeViewModel.getArticle("diabetes", "health", "id", ArticleApiConfig.API_KEY)
        }

        menuClickListener()
        binding.greetingMessage.text = messageGreeting()

        binding.btnCekDiabetes.setOnClickListener {
            val intent = Intent(activity, CekDiabetesActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setRecyclerView() {
        val articleLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val videoLayoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        if (binding.rvArticle.layoutManager == null) {
            binding.rvArticle.layoutManager = articleLayoutManager
        }

        articleAdapter = ArticleAdapter(isHorizontal = true)
        binding.rvArticle.adapter = articleAdapter

        binding.rvArticle.addItemDecoration(
            HorizontalItemDecoration(
                sideSpacing = resources.getDimensionPixelSize(R.dimen.recycler_view_side_spacing),
                betweenSpacing = resources.getDimensionPixelSize(R.dimen.recycler_view_between_spacing)
            )
        )

        if (binding.rvVideo.layoutManager == null) {
            binding.rvVideo.layoutManager = videoLayoutManager
        }

        videoAdapter = VideoAdapter(isHorizontal = true)
        binding.rvVideo.adapter = videoAdapter

        binding.rvVideo.addItemDecoration(
            HorizontalItemDecoration(
                sideSpacing = resources.getDimensionPixelSize(R.dimen.recycler_view_side_spacing),
                betweenSpacing = resources.getDimensionPixelSize(R.dimen.recycler_view_between_spacing)
            )
        )
    }


    private fun observeViewModel() {
        homeViewModel.articleData.observe(viewLifecycleOwner) { articleResponse ->
            articleResponse?.let {
                val limitArticles = it.articles.take(5)
                articleAdapter.submitList(limitArticles)
                binding.progressBarArticle.visibility = View.GONE
            } ?: run {
                binding.progressBarArticle.visibility = View.GONE
                Toast.makeText(requireContext(), "Fetching data error", Toast.LENGTH_SHORT).show()
            }
        }
        homeViewModel.videoData.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    val videoList = result.data
                    videoAdapter.submitList(videoList.take(5))
                    binding.progressBarVideo.visibility = View.GONE
                }
                is Result.Error -> {
                    binding.progressBarVideo.visibility = View.GONE
                    Toast.makeText(requireContext(), "Fetching data error", Toast.LENGTH_SHORT).show()
                }
                is Result.Loading -> {
                    binding.progressBarVideo.visibility = View.VISIBLE
                }
            }
        }
        homeViewModel.diabetesResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                }
                is Result.Success -> {
                    displayDiabetesResult(result.data.data)
                }
                is Result.Error -> {
                    showToast(result.error)
                }
            }
        }
        homeViewModel.dataResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                }
                is Result.Success -> {
                    displayData(result.data.data)
                }
                is Result.Error -> {
                    showToast(result.error)
                }
            }
        }
        homeViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarArticle.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.progressBarVideo.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun displayData(profile: RegisterData?) {
        if (profile != null) {
            binding.userFullName.text = profile.name ?: getString(R.string.nama_lengkap)

            if (profile.profileImageUrl is String) {
                Glide.with(this)
                    .load(profile.profileImageUrl)
                    .placeholder(R.drawable.avatar_placeholder)
                    .into(binding.profileImage)
            } else {
                Glide.with(this)
                    .load(R.drawable.avatar_placeholder)
                    .into(binding.profileImage)
            }
        }
    }
    private fun displayDiabetesResult(result: GetData?) {
        if(result != null) {
            binding.tvCategories.text = result.result ?: getString(R.string.tidak_ada_data)
        }
    }
    private fun messageGreeting(): String {
        val calendar = java.util.Calendar.getInstance()
        val currentHour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        Log.d("GreetingMessage", "Current Hour: $currentHour")

        return when (currentHour) {
            in 5..11 -> "Selamat Pagi!"
            in 12..14 -> "Selamat Siang!"
            in 15..17 -> "Selamat Sore!"
            else -> "Selamat Malam!"
        }
    }
    private fun menuClickListener() {
        binding.glucoseMenu.setOnClickListener {
            startActivity(Intent(activity, PemantauanGulaDarahActivity::class.java))
        }

        binding.mealMenu.setOnClickListener {
            startActivity(Intent(activity, FoodRecommendationActivity::class.java))
        }

        binding.videoMenu.setOnClickListener {
            startActivity(Intent(activity, VideoEdukasiActivity::class.java))
        }

        binding.articleMenu.setOnClickListener {
            startActivity(Intent(activity, ArtikelActivity::class.java))
        }

        binding.btnCekDiabetes.setOnClickListener {
            startActivity(Intent(activity, CekDiabetesActivity::class.java))
        }

        binding.tvArticleSeeDetail.setOnClickListener{
            startActivity(Intent(activity, ArtikelActivity::class.java))
        }

        binding.tvVideoSeeDetail.setOnClickListener{
            startActivity(Intent(activity, VideoEdukasiActivity::class.java))
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.getUserData()
        homeViewModel.getDiabetesCheckData()
    }
}