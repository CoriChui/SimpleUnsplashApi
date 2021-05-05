package com.example.imagesearchmvvmpagination.ui.gallery

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.imagesearchmvvmpagination.R
import com.example.imagesearchmvvmpagination.data.UnsplashPhoto
import com.example.imagesearchmvvmpagination.databinding.FragmentGalleryBinding
import com.example.imagesearchmvvmpagination.utils.VerticalSpacingItemDecorator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GalleryFragment : Fragment(R.layout.fragment_gallery), UnsplashPhotoAdapter.OnItemListener {

    private val viewModel by viewModels<GalleryViewModel>()
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private lateinit var unsplashAdapter: UnsplashPhotoAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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
        addViewModelObserver()
        initListeners()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_gallery, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    binding.recyclerView.scrollToPosition(0)
                    viewModel.searchPhotos(query)
                    searchView.clearFocus()
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initUnsplashAdapter() {
        unsplashAdapter = UnsplashPhotoAdapter(this).apply {
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
        with(binding.recyclerView) {
            setHasFixedSize(true)
            itemAnimator = null
            addItemDecoration(VerticalSpacingItemDecorator(16, 16))
            adapter = unsplashAdapter.withLoadStateHeaderAndFooter(
                header = UnsplashPhotoLoadStateAdapter { unsplashAdapter.retry() },
                footer = UnsplashPhotoLoadStateAdapter { unsplashAdapter.retry() }
            )
        }
    }

    private fun addViewModelObserver() {
        viewModel.photos.observe(viewLifecycleOwner) {
            unsplashAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    private fun initListeners() {
        binding.buttonRetry.setOnClickListener {
            unsplashAdapter.retry()
        }
    }

    override fun onItemClick(photo: UnsplashPhoto) {
        val action = GalleryFragmentDirections.actionGalleryFragmentToDetailsFragment(photo)
        findNavController().navigate(action)
    }
}