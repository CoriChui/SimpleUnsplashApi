package com.kaonstudio.imagesearchmvipagination.domain

import java.io.Serializable


data class UnsplashPhotoDomain(
    val id: String,
    val description: String?,
    val urls: UnsplashPhotoUrls,
    val user: UnsplashUser
) : Serializable {
    data class UnsplashPhotoUrls(
        val raw: String,
        val full: String,
        val regular: String,
        val small: String,
        val thumb: String,
    ) : Serializable

    data class UnsplashUser(
        val name: String,
        val username: String
    ) : Serializable {
        val attributionUrl get() = "https://unsplash.com/$username?utm_source=ImageSearchMvvmPagination&utm_medium=referral"
    }
}

