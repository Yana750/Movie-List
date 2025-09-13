package com.example.films.fragments

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.films.R
import com.example.films.view.FilmsAdapter
import com.example.films.viewmodel.FilmsViewModel
import kotlinx.coroutines.flow.collectLatest

class FilmsFragment : Fragment(R.layout.fragment_films) {

    private val viewModel: FilmsViewModel by viewModels()
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var genresRecyclerView: RecyclerView
    private lateinit var filmsRecyclerView: RecyclerView
    private lateinit var filmsAdapter: FilmsAdapter
    private lateinit var genresAdapter: GenresAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        genresRecyclerView = view.findViewById(R.id.genresRecyclerView)
        filmsRecyclerView = view.findViewById(R.id.recyclerView)

        // Адаптер фильмов
        filmsAdapter = FilmsAdapter { film ->
            val action = FilmsFragmentDirections.actionFilmsFragmentToDetailFilmFragment(film)
            findNavController().navigate(action)
        }

        // Динамический GridLayoutManager
        val displayMetrics: DisplayMetrics = requireContext().resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val cardWidthPx = resources.getDimensionPixelSize(R.dimen.card_width)
        val cardMarginPx = resources.getDimensionPixelSize(R.dimen.card_margin) * 2
        val spanCount = (screenWidth / (cardWidthPx + cardMarginPx)).coerceAtLeast(1)

        val layoutManager = GridLayoutManager(requireContext(), spanCount)
        filmsRecyclerView.layoutManager = layoutManager
        filmsRecyclerView.adapter = filmsAdapter

        // Паддинги, чтобы центрировать карточки
        filmsRecyclerView.clipToPadding = false
        val totalWidth = spanCount * cardWidthPx + (spanCount - 1) * cardMarginPx
        val sidePadding = ((screenWidth - totalWidth) / 2).coerceAtLeast(0)
        filmsRecyclerView.setPadding(sidePadding, 0, sidePadding, 0)

        // Адаптер жанров
        genresAdapter = GenresAdapter(emptyList()) { genre ->
            viewModel.selectGenre(genre)
            updateFilmsList()
        }
        genresRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        genresRecyclerView.adapter = genresAdapter

        swipeRefresh.setOnRefreshListener {
            viewModel.refreshFilms()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collectLatest { state ->
                swipeRefresh.isRefreshing = state.isLoading
                filmsRecyclerView.visibility = if (!state.isLoading) View.VISIBLE else View.GONE

                state.error?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }

                genresAdapter.updateGenres(state.genres, state.selectedGenre)
                updateFilmsList()
            }
        }
    }

    private fun updateFilmsList() {
        val sortedFilms = viewModel.getFilteredFilms().sortedBy { it.localized_name }
        filmsAdapter.submitList(sortedFilms)
    }
}
