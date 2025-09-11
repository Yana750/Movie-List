package com.example.films.ui

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.films.R
import com.example.films.view.FilmsAdapter
import com.example.films.viewmodel.FilmsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.core.graphics.toColorInt

class FilmsFragment : Fragment() {

    private val viewModel: FilmsViewModel by viewModels()
    private val adapter = FilmsAdapter()

    private lateinit var progressBar: ProgressBar
    private lateinit var genresTitle: TextView
    private lateinit var genresList: ListView
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        // AppBar
        val appBar = TextView(requireContext()).apply {
            text = "Фильмы"
            textSize = 20f
            typeface = ResourcesCompat.getFont(requireContext(), R.font.roboto_bold)
            setBackgroundColor("#0E3165".toColorInt())
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                56.dp
            )
        }
        root.addView(appBar)

        genresTitle = TextView(requireContext()).apply {
            text = "Жанры"
            textSize = 20f
            setPadding(16.dp, 8.dp, 16.dp, 8.dp)
            typeface = ResourcesCompat.getFont(requireContext(), R.font.roboto_bold)
            setOnClickListener { genresList.visibility = if (genresList.isGone) View.VISIBLE else View.GONE }
        }
        root.addView(genresTitle)

        genresList = ListView(requireContext()).apply { visibility = View.GONE }
        root.addView(genresList)

        val filmsTitle = TextView(requireContext()).apply {
            text = "Фильмы"
            textSize = 20f
            setPadding(16.dp, 8.dp, 16.dp, 8.dp)
            typeface = ResourcesCompat.getFont(requireContext(), R.font.roboto_bold)
        }
        root.addView(filmsTitle)

        progressBar = ProgressBar(requireContext()).apply { visibility = View.GONE }
        root.addView(progressBar)

        recyclerView = RecyclerView(requireContext()).apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@FilmsFragment.adapter
            setPadding(16.dp, 16.dp, 16.dp, 16.dp)
            clipToPadding = false
        }
        root.addView(recyclerView)

        observeViewModel()

        return root
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                genresTitle.visibility = if (!state.isLoading) View.VISIBLE else View.GONE
                recyclerView.visibility = if (!state.isLoading) View.VISIBLE else View.GONE

                state.error?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }

                val adapterGenres = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, state.genres) {
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                        val view = super.getView(position, convertView, parent) as TextView
                        view.typeface = ResourcesCompat.getFont(requireContext(), R.font.roboto_regular)
                        view.textSize = 18f
                        view.setPadding(16.dp, 12.dp, 16.dp, 12.dp)
                        if (state.selectedGenre == getItem(position)) view.setBackgroundColor("#FFC967".toColorInt())
                        else view.setBackgroundColor(Color.TRANSPARENT)
                        return view
                    }
                }
                genresList.adapter = adapterGenres
                genresList.setOnItemClickListener { _, _, position, _ -> viewModel.selectGenre(state.genres[position]) }

                adapter.submitList(viewModel.getFilteredFilms())
            }
        }
    }

    private val Int.dp: Int
        get() = (this * resources.displayMetrics.density).toInt()
}
