package com.kaonstudio.imagesearchmvipagination.ui.gallery

import com.kaonstudio.imagesearchmvipagination.domain.UnsplashPhotoDomain
import kotlinx.coroutines.CoroutineScope
import retrofit2.http.Query

sealed class GalleryViewEvent {
    data class QueryRequestEvent(var query: String = "dogs") : GalleryViewEvent()
    object RetryEvent : GalleryViewEvent()
    object RetryAdapterEvent : GalleryViewEvent()
    object StopLoadingEvent : GalleryViewEvent()
    data class OnPhotoClicked(val photo: UnsplashPhotoDomain) : GalleryViewEvent()
}
