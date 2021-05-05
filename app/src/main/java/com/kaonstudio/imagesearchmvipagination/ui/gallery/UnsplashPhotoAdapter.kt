package com.kaonstudio.imagesearchmvipagination.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.kaonstudio.imagesearchmvipagination.R
import com.kaonstudio.imagesearchmvipagination.databinding.ItemUnsplashPhotoBinding
import com.kaonstudio.imagesearchmvipagination.domain.UnsplashPhotoDomain
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalCoroutinesApi
@FlowPreview
class UnsplashPhotoAdapter (val photoClicked: (UnsplashPhotoDomain) -> Unit) :
    PagingDataAdapter<UnsplashPhotoDomain, UnsplashPhotoAdapter.PhotoViewHolder>(PHOTO_COMPARATOR) {

    inner class PhotoViewHolder(private val binding: ItemUnsplashPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        photoClicked(item)
                    }
                }
            }
        }

        fun bind(photo: UnsplashPhotoDomain) {
            binding.apply {
                Glide.with(itemView)
                    .load(photo.urls.regular)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_error)
                    .into(imageView)
                textViewUserName.text = photo.user.username
            }
        }
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding =
            ItemUnsplashPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    companion object {
        private val PHOTO_COMPARATOR = object : DiffUtil.ItemCallback<UnsplashPhotoDomain>() {
            override fun areItemsTheSame(oldItem: UnsplashPhotoDomain, newItem: UnsplashPhotoDomain): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: UnsplashPhotoDomain,
                newItem: UnsplashPhotoDomain
            ): Boolean =
                oldItem == newItem
        }
    }
}