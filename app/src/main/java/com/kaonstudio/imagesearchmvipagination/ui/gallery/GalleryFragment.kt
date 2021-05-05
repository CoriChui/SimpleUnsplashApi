package com.kaonstudio.imagesearchmvipagination.ui.gallery

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.imagesearchmvvmpagination.utils.VerticalSpacingItemDecorator
import com.kaonstudio.imagesearchmvipagination.R
import com.kaonstudio.imagesearchmvipagination.databinding.FragmentGalleryBinding
import com.kaonstudio.imagesearchmvipagination.model.Action
import com.kaonstudio.imagesearchmvipagination.model.NavModelStore
import com.kaonstudio.imagesearchmvipagination.model.NavRoutes
import com.kaonstudio.imagesearchmvipagination.utils.ViewEventFlow
import com.kaonstudio.imagesearchmvipagination.utils.clicks
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
@FlowPreview
@AndroidEntryPoint
class GalleryFragment : Fragment(), ViewEventFlow<GalleryViewEvent> {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private val galleryViewModel: GalleryViewModel by viewModels()
    private val scope = MainScope()
    lateinit var unsplashAdapter: UnsplashPhotoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        initUnsplashAdapter()
        setupRecyclerView()
        observeState()
        dispatchEvents()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_gallery, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotEmpty()) {
                        binding.recyclerView.scrollToPosition(0)
                        galleryViewModel.process(GalleryViewEvent.QueryRequestEvent(query))
                        searchView.clearFocus()
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun initUnsplashAdapter() {
        unsplashAdapter = UnsplashPhotoAdapter {
            galleryViewModel.process(GalleryViewEvent.OnPhotoClicked(it))
        }
            .apply {
                addLoadStateListener {
                    with(binding) {
                        progressBar.isVisible = it.source.refresh is LoadState.Loading
                        recyclerView.isVisible = it.source.refresh is LoadState.NotLoading
                        buttonRetry.isVisible = it.source.refresh is LoadState.Error
                        textViewError.isVisible = it.source.refresh is LoadState.Error

                        if (it.source.refresh is LoadState.NotLoading &&
                            it.append.endOfPaginationReached &&
                            unsplashAdapter.itemCount < 1
                        ) {
                            recyclerView.isVisible = false
                            textViewEmpty.isVisible = true
                        } else {
                            textViewEmpty.isVisible = false
                        }
                    }
                }
            }
    }

    private fun setupRecyclerView() {
        val loadAdapterListener = object : UnsplashPhotoLoadStateAdapter.LoadAdapterListener {
            override fun onRetry() {
                galleryViewModel.process(GalleryViewEvent.RetryAdapterEvent)
            }

            override fun onStop() {
                galleryViewModel.process(GalleryViewEvent.StopLoadingEvent)
            }

        }
        with(binding.recyclerView) {
            setHasFixedSize(true)
            itemAnimator = null
            addItemDecoration(VerticalSpacingItemDecorator(16, 16))
            adapter = unsplashAdapter.withLoadStateHeaderAndFooter(
                header = UnsplashPhotoLoadStateAdapter(loadAdapterListener),
                footer = UnsplashPhotoLoadStateAdapter(loadAdapterListener)
            )
        }
    }

    private fun observeState() {
        galleryViewModel.state()
            .onEach {
                with(it) {
                    when (action) {
                        is Action.Request -> photos?.let {
                            unsplashAdapter.submitData(
                                viewLifecycleOwner.lifecycle,
                                photos
                            )
                        }
                        is Action.Retry -> unsplashAdapter.retry()
                        is Action.Idle -> {
                        }
                    }
                }
            }
            .launchIn(scope)

        NavModelStore.indistinctState()
            .onEach {
                with(it) {
                    routes?.let { route ->
                        when (route) {
                            is NavRoutes.ToDetails -> {
                                val photo = route.getPhoto()
                                photo?.let {
                                    val action =
                                        GalleryFragmentDirections.actionGalleryFragmentToDetailsFragment(
                                            photo
                                        )
                                    findNavController().navigate(action)
                                }
                            }
                            else -> {
                            }
                        }
                    }
                }
            }
            .launchIn(scope)

    }

    private fun dispatchEvents() {
        viewEvents()
            .onEach { event -> galleryViewModel.process(event) }
            .launchIn(scope)
    }

    override fun viewEvents(): Flow<GalleryViewEvent> {
        val flows = listOf(
            binding.buttonRetry.clicks().map { GalleryViewEvent.RetryAdapterEvent }
        )
        return flows.asFlow().flattenMerge()
    }
}