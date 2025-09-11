package com.example.films.view

import android.content.res.Resources
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.films.R
import com.example.films.model.Film
import com.example.films.ui.DetailFilmFragment

class FilmsAdapter : RecyclerView.Adapter<FilmsAdapter.FilmViewHolder>() {

    private var films: List<Film> = emptyList()

    fun submitList(films: List<Film>) {
        this.films = films
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val context = parent.context
        val displayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels

        // Паддинги и gap
        val recyclerPadding = 16.dp
        val gap = 8.dp

        // Вычисляем ширину и высоту карточки
        val cardWidth = (screenWidth - recyclerPadding * 2 - gap) / 2
        val cardHeight = (cardWidth.toFloat() / 160 * 270).toInt()

        // Контейнер карточки
        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(cardWidth, cardHeight).apply {
                setMargins(gap / 2, gap / 2, gap / 2, gap / 2)
            }
        }

        val imageView = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (cardHeight * 0.83).toInt() // 242/290 как в Figma
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
            clipToOutline = true
            outlineProvider = RoundedOutlineProvider(4.dp.toFloat())
        }

        val title = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            maxLines = 2
            ellipsize = android.text.TextUtils.TruncateAt.END
            textSize = 16f
            typeface = ResourcesCompat.getFont(context, R.font.roboto_bold)
            setPadding(4.dp, 4.dp, 4.dp, 4.dp)
        }

        container.addView(imageView)
        container.addView(title)

        return FilmViewHolder(container, imageView, title)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val film = films[position]

        // Заполняем данные
        holder.title.text = film.localized_name
        holder.image.load(film.image_url) {
            placeholder(R.drawable.img)
            error(R.drawable.img)
            fallback(R.drawable.img)
        }
        holder.container.setOnClickListener {
            val fragment = DetailFilmFragment().apply {
                arguments = Bundle().apply { putParcelable("film", film) }
            }
            val activity = holder.container.context as androidx.fragment.app.FragmentActivity
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }


    override fun getItemCount() = films.size

    class FilmViewHolder(
        val container: LinearLayout,
        val image: ImageView,
        val title: TextView
    ) : RecyclerView.ViewHolder(container)

    // Extension dp -> px
    private val Int.dp: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()
}
