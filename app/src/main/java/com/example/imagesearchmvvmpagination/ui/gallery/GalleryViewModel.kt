package com.example.imagesearchmvvmpagination.ui.gallery

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.example.imagesearchmvvmpagination.data.UnsplashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val repository: UnsplashRepository,
    private val state: SavedStateHandle
) : ViewModel() {

    private val currentQuery = state.getLiveData(CURRENT_QUERY, DEFAULT_VALUE)

    val photos = currentQuery.switchMap {
        repository.getSearchResult(it).cachedIn(viewModelScope)
    }

    fun searchPhotos(query: String) {
        currentQuery.value = query
    }

    companion object {
        private const val DEFAULT_VALUE = "cats"
        private const val CURRENT_QUERY = "current_query"
    }
}