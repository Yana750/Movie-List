package com.example.films.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.films.model.Film

class DetailFilmViewModelFactory(private val film: Film) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailFilmViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DetailFilmViewModel(film) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
