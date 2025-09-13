package com.example.films.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.films.R
import com.example.films.model.Film

class FilmsAdapter(
    private val onFilmClick: (Film) -> Unit
) : RecyclerView.Adapter<FilmsAdapter.FilmViewHolder>() {

    private var films: List<Film> = emptyList()

    fun submitList(films: List<Film>) {
        this.films = films
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_film, parent, false)
        return FilmViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val film = films[position]
        holder.title.text = film.localized_name
        holder.image.load(film.image_url) {
            placeholder(R.drawable.img)
            error(R.drawable.img)
        }

        // Колбэк клика безопасно передаем фрагменту
        holder.itemView.setOnClickListener {
            onFilmClick(film)
        }
    }

    override fun getItemCount(): Int = films.size

    class FilmViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.filmImage)
        val title: TextView = view.findViewById(R.id.filmsTitle)
    }
}
