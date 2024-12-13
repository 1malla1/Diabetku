package com.skye.diabetku.adapter


import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.skye.diabetku.R
import com.skye.diabetku.data.remote.response.ArticlesItem
import com.skye.diabetku.data.remote.response.VideoItem
import com.skye.diabetku.databinding.ItemRowVideoArtikelBinding
import com.skye.diabetku.databinding.ItemRowVideoArtikelHomeBinding

class VideoAdapter(private val isHorizontal: Boolean) : ListAdapter<VideoItem, VideoAdapter.MyViewHolder>(
    DIFF_CALLBACK
) {
    override fun getItemViewType(position: Int): Int {
        return if (isHorizontal) VIEW_TYPE_HORIZONTAL else VIEW_TYPE_VERTICAL
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = when (viewType) {
            VIEW_TYPE_HORIZONTAL -> ItemRowVideoArtikelHomeBinding.inflate(inflater, parent, false)
            VIEW_TYPE_VERTICAL -> ItemRowVideoArtikelBinding.inflate(inflater, parent, false)
            else -> throw IllegalArgumentException("Invalid viewType")
        }
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val video = getItem(position)
        holder.bind(video)
    }

    inner class MyViewHolder(private val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(video: VideoItem) {
            when (binding) {
                is ItemRowVideoArtikelHomeBinding -> {

                    val videoId = extractYouTubeId(video.link.toString())
                    val thumbnailUrl = "https://img.youtube.com/vi/$videoId/hqdefault.jpg"

                    Glide.with(binding.root.context)
                        .load(thumbnailUrl)
                        .placeholder(R.drawable.avatar_placeholder)
                        .error(R.drawable.broken_image)
                        .fitCenter()
                        .into(binding.imgItemThumbnail)
                    binding.tvItemTitle.text = video.title

                    binding.root.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(video.link))
                        binding.root.context.startActivity(intent)
                    }
                }

                is ItemRowVideoArtikelBinding -> {
                    val videoId = extractYouTubeId(video.link.toString())
                    val thumbnailUrl = "https://img.youtube.com/vi/$videoId/hqdefault.jpg"

                    Glide.with(binding.root.context)
                        .load(thumbnailUrl)
                        .placeholder(R.drawable.avatar_placeholder)
                        .error(R.drawable.broken_image)
                        .fitCenter()
                        .into(binding.imgItemThumbnail)
                    binding.tvItemTitle.text = video.title

                    binding.root.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(video.link))
                        binding.root.context.startActivity(intent)
                    }
                }
            }
        }
    }
    private fun extractYouTubeId(url: String): String? {
        val regex = "(?<=youtu.be\\/|youtube.com\\/watch\\?v=)[^&\\/\\?]+".toRegex()
        val matchResult = regex.find(url)
        return matchResult?.value
    }

    companion object {
        private const val VIEW_TYPE_VERTICAL = 0
        private const val VIEW_TYPE_HORIZONTAL = 1

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<VideoItem>() {
            override fun areItemsTheSame(
                oldItem: VideoItem,
                newItem: VideoItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: VideoItem,
                newItem: VideoItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}