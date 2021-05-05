package com.kaonstudio.imagesearchmvipagination.ui.gallery

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.kaonstudio.imagesearchmvipagination.domain.UnsplashPhotoDomain
import com.kaonstudio.imagesearchmvipagination.intent.Intent
import com.kaonstudio.imagesearchmvipagination.intent.IntentFactory
import com.kaonstudio.imagesearchmvipagination.intent.intent
import com.kaonstudio.imagesearchmvipagination.intent.sideEffect
import com.kaonstudio.imagesearchmvipagination.model.*
import com.kaonstudio.imagesearchmvipagination.network.UnsplashRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import java.io.BufferedReader
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val unsplashRepository: UnsplashRepository,
    private val unsplashModelStore: UnsplashModelStore,
    private val state: SavedStateHandle
) : ViewModel(), IntentFactory<GalleryViewEvent> {

    private val scope = MainScope()

    init {
        scope.launch {
            process(GalleryViewEvent.QueryRequestEvent())
        }
    }

    fun state(): Flow<GalleryState> = unsplashModelStore.state()

    override fun process(viewEvent: GalleryViewEvent) {
        unsplashModelStore.process(toIntent(viewEvent))
    }

    private fun toIntent(viewEvent: GalleryViewEvent): Intent<GalleryState> {
        return when (viewEvent) {
            is GalleryViewEvent.QueryRequestEvent -> queryRequest(viewEvent.query)
            is GalleryViewEvent.RetryEvent -> retry()
            is GalleryViewEvent.OnPhotoClicked -> navigateToDetails(viewEvent.photo)
            is GalleryViewEvent.RetryAdapterEvent -> adapterRetry()
            is GalleryViewEvent.StopLoadingEvent -> setIdle()
            else -> sideEffect { }
        }
    }

    private fun queryRequest(query: String): Intent<GalleryState> = sideEffect {
        scope.launch {
            unsplashRepository.getSearchResult(query).cachedIn(viewModelScope)
                .collect { pagingData ->
                    val intent = intent<GalleryState> {
                        copy(photos = pagingData, query = query, action = Action.Request)
                    }
                    unsplashModelStore.process(intent)
                }
        }
    }

    private fun retry(): Intent<GalleryState> = sideEffect {
        scope.launch {
            if (!query.isNullOrEmpty()) {
                unsplashRepository.getSearchResult(query).cachedIn(viewModelScope)
                    .collect { pagingData ->
                        val intent = intent<GalleryState> {
                            copy(action = Action.Request)
                        }
                        unsplashModelStore.process(intent)
                    }
            }
        }
    }

    private fun adapterRetry(): Intent<GalleryState> = intent {
        copy(action = Action.Retry)
    }

    private fun setIdle(): Intent<GalleryState> = intent {
        copy(action = Action.Idle)
    }


    private fun navigateToDetails(photo: UnsplashPhotoDomain): Intent<GalleryState> = sideEffect {
        val navIntent = intent<NavState> {
            copy(routes = NavRoutes.ToDetails(photo))
        }
        NavModelStore.process(navIntent)
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}