package com.kaonstudio.imagesearchmvipagination.network

import androidx.paging.*
import com.kaonstudio.imagesearchmvipagination.domain.UnsplashPhotoDomain
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnsplashRepository @Inject constructor(
    private val unsplashApi: UnsplashApi,
    private val dtoPhotoMapper: DtoPhotoMapper
) {

    fun getSearchResult(query: String) =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { UnsplashPagingSource(unsplashApi, query) }
        ).flow.map { pagingData ->
            pagingData.map {dtoPhotoMapper.mapFromEntity(it)}
        }
}