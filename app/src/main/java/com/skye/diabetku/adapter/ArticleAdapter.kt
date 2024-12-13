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
import com.skye.diabetku.databinding.ItemRowVideoArtikelBinding
import com.skye.diabetku.databinding.ItemRowVideoArtikelHomeBinding

class ArticleAdapter(private val isHorizontal: Boolean) : ListAdapter<ArticlesItem, ArticleAdapter.MyViewHolder>(
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
        val article = getItem(position)
        holder.bind(article)
    }

    inner class MyViewHolder(private val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(article: ArticlesItem) {
            when (binding) {
                is ItemRowVideoArtikelHomeBinding -> {
                    Glide.with(binding.root.context)
                        .load(article.urlToImage)
                        .placeholder(R.drawable.avatar_placeholder)
                        .error(R.drawable.broken_image)
                        .fitCenter()
                        .into(binding.imgItemThumbnail)
                    binding.tvItemTitle.text = article.title

                    binding.root.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                        binding.root.context.startActivity(intent)
                    }
                }

                is ItemRowVideoArtikelBinding -> {
                    Glide.with(binding.root.context)
                        .load(article.urlToImage)
                        .placeholder(R.drawable.avatar_placeholder)
                        .error(R.drawable.broken_image)
                        .fitCenter()
                        .into(binding.imgItemThumbnail)
                    binding.tvItemTitle.text = article.title

                    binding.root.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                        binding.root.context.startActivity(intent)
                    }
                }
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_VERTICAL = 0
        private const val VIEW_TYPE_HORIZONTAL = 1

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ArticlesItem>() {
            override fun areItemsTheSame(
                oldItem: ArticlesItem,
                newItem: ArticlesItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ArticlesItem,
                newItem: ArticlesItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}