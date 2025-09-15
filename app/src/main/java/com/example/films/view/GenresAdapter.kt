package com.example.films.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.example.films.R

class GenresAdapter(
    private val onGenreClick: (String) -> Unit
) : RecyclerView.Adapter<GenresAdapter.GenreViewHolder>() {

    private var genres: List<String> = emptyList()
    private var selectedGenre: String? = null
    private var isExpanded: Boolean = false

    // Обновляет данные, НЕ сбрасывает isExpanded
    fun updateGenres(newGenres: List<String>, selected: String?) {
        genres = newGenres
        selectedGenre = selected
        notifyDataSetChanged()
    }

    // Вызывается из Fragment при клике на заголовок
    fun toggleExpanded() {
        isExpanded = !isExpanded
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (isExpanded) genres.size else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_genre, parent, false)
        return GenreViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        val genre = genres[position]
        holder.bind(genre, genre == selectedGenre)
    }

    inner class GenreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val genreText: TextView = view.findViewById(R.id.genreText)

        fun bind(name: String, isSelected: Boolean) {
            genreText.text = name
            genreText.typeface = ResourcesCompat.getFont(itemView.context, R.font.roboto_regular)
            genreText.textSize = 18f
            genreText.setBackgroundColor(
                if (isSelected) "#FFC967".toColorInt()
                else android.graphics.Color.TRANSPARENT
            )
            genreText.setOnClickListener {
                onGenreClick(name)
            }
        }
    }
}
