package com.kaonstudio.imagesearchmvipagination.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kaonstudio.imagesearchmvipagination.databinding.UnsplashPhotoLoadStateFooterBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
class UnsplashPhotoLoadStateAdapter (val loadAdapterListener: LoadAdapterListener) :
    LoadStateAdapter<UnsplashPhotoLoadStateAdapter.LoadStateViewHolder>() {

    inner class LoadStateViewHolder(private val binding: UnsplashPhotoLoadStateFooterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.buttonRetry.setOnClickListener {
                loadAdapterListener.onRetry()
            }
        }

        fun bind(loadState: LoadState) {
            binding.apply {
                if (loadState !is LoadState.Loading) {
                    loadAdapterListener.onStop()
                }
                progressBar.isVisible = loadState is LoadState.Loading
                buttonRetry.isVisible = loadState !is LoadState.Loading
                textViewError.isVisible = loadState !is LoadState.Loading
            }
        }
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val binding = UnsplashPhotoLoadStateFooterBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LoadStateViewHolder(binding)
    }

    interface LoadAdapterListener {
        fun onRetry()
        fun onStop()
    }
}