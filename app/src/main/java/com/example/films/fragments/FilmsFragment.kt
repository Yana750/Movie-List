package com.example.films.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.films.R
import com.example.films.view.FilmsAdapter
import com.example.films.view.GenresAdapter
import com.example.films.viewmodel.FilmsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest

class FilmsFragment : Fragment(R.layout.fragment_films) {

    private val viewModel: FilmsViewModel by viewModels()
    private lateinit var filmsAdapter: FilmsAdapter
    private lateinit var genresAdapter: GenresAdapter
    private lateinit var filmsRecyclerView: RecyclerView
    private lateinit var genresRecyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var genresTitle: TextView
    private lateinit var initialProgress: ProgressBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация view
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        filmsRecyclerView = view.findViewById(R.id.recyclerView)
        genresRecyclerView = view.findViewById(R.id.genresRecyclerView)
        genresTitle = view.findViewById(R.id.genresTitle)
        initialProgress = view.findViewById(R.id.initialProgress)

        setupFilmsRecyclerView()
        setupGenresRecyclerView()
        setupSwipeRefresh()

        observeUiState()
    }

    private fun setupFilmsRecyclerView() {
        filmsAdapter = FilmsAdapter { film ->
            val action = FilmsFragmentDirections.actionFilmsFragmentToDetailFilmFragment(film)
            findNavController().navigate(action)
        }

        val displayMetrics: DisplayMetrics = requireContext().resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val cardWidthPx = resources.getDimensionPixelSize(R.dimen.card_width)
        val cardMarginPx = resources.getDimensionPixelSize(R.dimen.card_margin) * 2
        val spanCount = (screenWidth / (cardWidthPx + cardMarginPx)).coerceAtLeast(1)

        val layoutManager = GridLayoutManager(requireContext(), spanCount)
        filmsRecyclerView.layoutManager = layoutManager
        filmsRecyclerView.adapter = filmsAdapter

        // Центрируем карточки по ширине
        filmsRecyclerView.clipToPadding = false
        val totalWidth = spanCount * cardWidthPx + (spanCount - 1) * cardMarginPx
        val sidePadding = ((screenWidth - totalWidth) / 2).coerceAtLeast(0)
        filmsRecyclerView.setPadding(sidePadding, 0, sidePadding, 0)
    }

    private fun setupGenresRecyclerView() {
        genresAdapter = GenresAdapter { genre ->
            viewModel.selectGenre(genre)
            genresAdapter.toggleExpanded()
        }

        genresRecyclerView.adapter = genresAdapter
        genresRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Клик по заголовку "Жанры" — раскрыть/свернуть список
        genresTitle.setOnClickListener {
            genresAdapter.toggleExpanded()
        }
    }

    private fun setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener {
            viewModel.refreshFilms()
        }
    }

    private fun observeUiState() {
        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collectLatest { uiState ->
                // Индикатор pull-to-refresh
                swipeRefresh.isRefreshing = uiState.isLoading && filmsAdapter.itemCount > 0

                // ProgressBar первичная загрузка
                initialProgress.visibility =
                    if (uiState.isLoading && filmsAdapter.itemCount == 0) View.VISIBLE else View.GONE

                // Обновление списка фильмов
                filmsAdapter.submitList(uiState.filteredFilms)

                // Обновление списка жанров
                genresAdapter.updateGenres(uiState.genres, uiState.selectedGenre)

                // Snackbar при ошибке
                uiState.error?.let {
                    view?.let { root ->
                        Snackbar.make(root, "Ошибка подключения сети", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Повторить") {
                                viewModel.refreshFilms()
                            }
                            .setBackgroundTint("#232323".toColorInt())
                            .setTextColor(Color.WHITE)
                            .show()
                    }
                }
            }
        }
    }
}
