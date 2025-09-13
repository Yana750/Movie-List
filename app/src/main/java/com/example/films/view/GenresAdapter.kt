package com.example.films.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.example.films.R

class GenresAdapter(
    private var genres: List<String>,
    private val onGenreClick: (String) -> Unit
) : RecyclerView.Adapter<GenresAdapter.GenreViewHolder>() {

    private var isExpanded = false
    private var selectedGenre: String? = null

    fun updateGenres(newGenres: List<String>, selected: String?) {
        genres = newGenres
        selectedGenre = selected
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = 1 + if (isExpanded) genres.size else 0 // 1 для заголовка

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_genre, parent, false)
        return GenreViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        if (position == 0) {
            // Заголовок "Жанры"
            holder.genreHeader.visibility = View.VISIBLE
            holder.genreText.visibility = View.GONE
            holder.genreHeader.setOnClickListener {
                isExpanded = !isExpanded
                notifyDataSetChanged()
            }
        } else {
            val genre = genres[position - 1]
            holder.genreHeader.visibility = View.GONE
            holder.genreText.visibility = View.VISIBLE
            holder.genreText.text = genre
            holder.genreText.typeface = ResourcesCompat.getFont(holder.itemView.context, R.font.roboto_regular)
            holder.genreText.textSize = 18f
            holder.genreText.setBackgroundColor(
                if (genre == selectedGenre) "#FFC967".toColorInt()
                else android.graphics.Color.TRANSPARENT
            )
            holder.genreText.setOnClickListener {
                selectedGenre = genre
                onGenreClick(genre)
                notifyDataSetChanged()
            }
        }
    }

    class GenreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val genreHeader: TextView = itemView.findViewById(R.id.genreHeader)
        val genreText: TextView = itemView.findViewById(R.id.genreText)
    }
}
