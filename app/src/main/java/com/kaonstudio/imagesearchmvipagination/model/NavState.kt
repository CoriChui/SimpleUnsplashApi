package com.kaonstudio.imagesearchmvipagination.model

import com.kaonstudio.imagesearchmvipagination.domain.UnsplashPhotoDomain

data class NavState(
    var routes: NavRoutes? = null
)

sealed class NavRoutes {
    data class ToDetails(private val photo: UnsplashPhotoDomain) : NavRoutes() {
        var isHandled = false
        fun getPhoto() : UnsplashPhotoDomain? {
            return if (isHandled) {
                null
            } else {
                isHandled = true
                photo
            }
        }
    }

    object None : NavRoutes()
    object ToGallery : NavRoutes()
}