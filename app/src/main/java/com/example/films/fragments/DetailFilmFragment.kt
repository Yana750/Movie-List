package com.example.films.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.films.R
import com.example.films.viewmodel.DetailFilmViewModel
import com.example.films.viewmodel.DetailFilmViewModelFactory
import com.google.android.material.appbar.MaterialToolbar

class DetailFilmFragment : Fragment(R.layout.fragment_detail_film) {

    private val args: DetailFilmFragmentArgs by navArgs()

    private val viewModel: DetailFilmViewModel by viewModels {
        DetailFilmViewModelFactory(args.film)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: MaterialToolbar = view.findViewById(R.id.toolbar)
        val imageView: ImageView = view.findViewById(R.id.filmImage)
        val localizedTitle: TextView = view.findViewById(R.id.localizedTitle)
        val genresYear: TextView = view.findViewById(R.id.genresYear)
        val ratingText: TextView = view.findViewById(R.id.ratingText)
        val description: TextView = view.findViewById(R.id.description)

        // Настройка Toolbar
        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Подписывается на данные из ViewModel
        viewModel.filmLiveData.observe(viewLifecycleOwner) { film ->
            toolbar.title = film.localized_name
            imageView.load(film.image_url ?: R.drawable.img) {
                placeholder(R.drawable.img)
                error(R.drawable.img)
            }
            localizedTitle.text = film.localized_name
            genresYear.text = "${film.genres.joinToString(", ")}, ${film.year} год"
            description.text = film.description ?: "Описание отсутствует"
        }

        viewModel.ratingSpannable.observe(viewLifecycleOwner) { spannable ->
            ratingText.text = spannable
        }
    }
}
