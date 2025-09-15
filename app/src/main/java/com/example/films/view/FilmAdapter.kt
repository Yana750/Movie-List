package com.example.films.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.films.R
import com.example.films.model.Film

class FilmsAdapter(
    private val onFilmClick: (Film) -> Unit
) : ListAdapter<Film, FilmsAdapter.FilmViewHolder>(DiffCallback) {
    object DiffCallback : DiffUtil.ItemCallback<Film>() {
        override fun areItemsTheSame(oldItem: Film, newItem: Film): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Film, newItem: Film): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_film, parent, false)
        return FilmViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FilmViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val image: ImageView = view.findViewById(R.id.filmImage)
        private val title: TextView = view.findViewById(R.id.filmsTitle)

        fun bind(film: Film) {
            title.text = film.localized_name
            image.load(film.image_url) {
                placeholder(R.drawable.img)
                error(R.drawable.img)
            }
            itemView.setOnClickListener { onFilmClick(film) }
        }
    }
}
