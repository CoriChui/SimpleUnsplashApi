package com.kaonstudio.imagesearchmvipagination.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import java.lang.Exception

private const val UNSPLASH_STARTING_PAGE = 1

class UnsplashPagingSource(
    private val unsplashApi: UnsplashApi,
    private val query: String
) : PagingSource<Int, UnsplashPhotoDto>(){
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnsplashPhotoDto> {
        val position = params.key ?: UNSPLASH_STARTING_PAGE

        return try {
            val response = unsplashApi.searchPhotos(query = query, position, params.loadSize)
            val photos = response.results
            LoadResult.Page(
                data = photos,
                prevKey = if (position == UNSPLASH_STARTING_PAGE) null else position - 1,
                nextKey = if (photos.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, UnsplashPhotoDto>): Int? {
        return state.anchorPosition
    }
}