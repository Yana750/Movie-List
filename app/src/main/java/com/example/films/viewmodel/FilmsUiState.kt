package com.example.films.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.films.model.Film
import com.example.films.model.FilmsApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class FilmsUiState(
    val isLoading: Boolean = false,
    val films: List<Film> = emptyList(),
    val genres: List<String> = emptyList(),
    val error: String? = null,
    val selectedGenre: String? = null
) {
    val filteredFilms: List<Film>
        get() = selectedGenre?.let {genre ->
            films.filter { it.genres.contains(genre) }
        } ?: films
}

class FilmsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FilmsUiState())
    val uiState: StateFlow<FilmsUiState> = _uiState

    private val api = FilmsApi.create()

    init {
        loadFilms()
    }

    fun loadFilms() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val films = api.getFilms().films.sortedBy { it.localized_name }
                val genres = films.flatMap { it.genres }.distinct().sorted()
                _uiState.value = FilmsUiState(
                    films = films,
                    genres = genres,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = FilmsUiState(
                    isLoading = false,
                    error = e.message ?: "Ошибка загрузки"
                )
            }
        }
    }

    fun selectGenre(genre: String?) {
        val current = _uiState.value.selectedGenre
        _uiState.value = if (current == genre) {
            _uiState.value.copy(selectedGenre = null)
        } else {
            _uiState.value.copy(selectedGenre = genre)
        }
    }

    fun getFilteredFilms(): List<Film> {
        val genre = _uiState.value.selectedGenre
        return if (genre == null) {
            _uiState.value.films.sortedBy { it.localized_name }
        } else {
            _uiState.value.films.filter { it.genres.contains(genre) }
                .sortedBy { it.localized_name }
        }
    }
}
