package com.kaonstudio.imagesearchmvipagination.model

import androidx.paging.PagingData
import com.kaonstudio.imagesearchmvipagination.domain.UnsplashPhotoDomain

data class GalleryState(
    val photos: PagingData<UnsplashPhotoDomain>? = null,
    val query: String? = null,
    val action: Action = Action.Idle
) {

}

sealed class Action {
    object Retry : Action()
    object Request : Action()
    object Idle : Action()
}

