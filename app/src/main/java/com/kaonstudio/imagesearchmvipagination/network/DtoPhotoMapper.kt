package com.kaonstudio.imagesearchmvipagination.network

import com.kaonstudio.imagesearchmvipagination.domain.UnsplashPhotoDomain
import com.kaonstudio.imagesearchmvipagination.utils.EntityMapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DtoPhotoMapper @Inject constructor(): EntityMapper<UnsplashPhotoDto, UnsplashPhotoDomain> {
    override fun mapFromEntity(entity: UnsplashPhotoDto): UnsplashPhotoDomain {
        return UnsplashPhotoDomain(
            id = entity.id,
            description = entity.description,
            urls = UnsplashPhotoDomain.UnsplashPhotoUrls(
                raw = entity.urls.raw,
                full = entity.urls.full,
                regular = entity.urls.regular,
                small = entity.urls.small,
                thumb = entity.urls.thumb,
            ),
            user = UnsplashPhotoDomain.UnsplashUser(
                name = entity.user.name,
                username = entity.user.username
            )
        )
    }

    fun mapFromEntityList(entities: List<UnsplashPhotoDto>) : List<UnsplashPhotoDomain> {
        return entities.map(::mapFromEntity)
    }
}