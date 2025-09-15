package com.example.films.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import com.example.films.model.Film

class DetailFilmViewModel(film: Film) : ViewModel() {

    private val _filmLiveData = MutableLiveData<Film>()
    val filmLiveData: LiveData<Film> get() = _filmLiveData

    private val _ratingSpannable = MutableLiveData<SpannableString>()
    val ratingSpannable: LiveData<SpannableString> get() = _ratingSpannable

    init {
        _filmLiveData.value = film
        _ratingSpannable.value = createRatingSpannable(film.rating)
    }

    @SuppressLint("DefaultLocale")
    private fun createRatingSpannable(rating: Double?): SpannableString {
        val ratingValue = String.format("%.1f", rating)
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
        return spannable
    }
}
