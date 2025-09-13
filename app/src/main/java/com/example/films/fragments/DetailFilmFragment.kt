package com.example.films.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.films.R
import com.example.films.model.Film
import com.google.android.material.appbar.MaterialToolbar

class DetailFilmFragment : Fragment(R.layout.fragment_detail_film) {

    private val args: DetailFilmFragmentArgs by navArgs()
    private lateinit var film: Film

    @SuppressLint("DefaultLocale", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Получаем фильм из аргументов
        film = args.film

        // Инициализация view
        val toolbar: MaterialToolbar = view.findViewById(R.id.toolbar)
        val imageView: ImageView = view.findViewById(R.id.filmImage)
        val localizedTitle: TextView = view.findViewById(R.id.localizedTitle)
        val genresYear: TextView = view.findViewById(R.id.genresYear)
        val ratingText: TextView = view.findViewById(R.id.ratingText)
        val description: TextView = view.findViewById(R.id.description)

        // Настройка Toolbar
        toolbar.title = film.localized_name
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        // Заполнение данных
        imageView.load(film.image_url) {
            placeholder(R.drawable.img)
            error(R.drawable.img)
        }
        localizedTitle.text = film.localized_name
        genresYear.text = "${film.genres.joinToString(", ")}, ${film.year} год"

        // Рейтинг с разным размером текста
        val ratingValue = String.format("%.1f", film.rating)
        val spannable = SpannableString("$ratingValue КиноПоиск")
        spannable.setSpan(
            AbsoluteSizeSpan(24, true),
            0,
            ratingValue.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            AbsoluteSizeSpan(16, true),
            ratingValue.length + 1,
            spannable.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        ratingText.text = spannable

        // Описание
        description.text = film.description ?: "Описание отсутствует"
    }
}
