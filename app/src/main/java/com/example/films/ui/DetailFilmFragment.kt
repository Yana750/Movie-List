package com.example.films.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.films.R
import com.example.films.model.Film

class DetailFilmFragment : Fragment() {

    companion object {
        const val ARG_FILM = "film"
    }

    private lateinit var film: Film

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        film = arguments?.getParcelable(ARG_FILM) ?: throw IllegalArgumentException("Film missing")
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ScrollView {
        val context = requireContext()

        // Корневой ScrollView
        val scrollView = ScrollView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        val root = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        scrollView.addView(root)

        // Шапка с названием и кнопкой назад
        val header = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor("#0E3165".toColorInt())
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                56.dp
            )
            gravity = Gravity.CENTER_VERTICAL
        }

        val backButton = ImageView(context).apply {
            setImageResource(R.drawable.vector)
            setPadding(16.dp, 0, 16.dp, 0)
            setOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }

        val titleText = TextView(context).apply {
            text = film.localized_name
            textSize = 20f
            setTextColor(Color.WHITE)
            typeface = ResourcesCompat.getFont(context, R.font.roboto_regular)
            maxLines = 2
            ellipsize = android.text.TextUtils.TruncateAt.END
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                weight = 1f
            }
        }

        header.addView(backButton)
        header.addView(titleText)
        root.addView(header)

        // Получаем ширину экрана
        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels

        // Делаем постер шириной ~1/3 экрана
        val posterWidth = (screenWidth / 3)
        val posterHeight = (posterWidth / 132f * 201f).toInt()

        val imageView = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                posterWidth,
                posterHeight
            ).apply {
                // расстояние сверху от AppBar
                topMargin = 18.dp
                gravity = Gravity.CENTER_HORIZONTAL
            }

            scaleType = ImageView.ScaleType.CENTER_CROP
            clipToOutline = true
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: android.view.View, outline: android.graphics.Outline) {
                    outline.setRoundRect(0, 0, view.width, view.height, 4.dp.toFloat())
                }
            }

            load(film.image_url) {
                placeholder(R.drawable.img)
                error(R.drawable.img)
                fallback(R.drawable.img)
            }
        }
        root.addView(imageView)

        // Локализованное название
        val localizedTitle = TextView(context).apply {
            text = film.localized_name
            textSize = 26f
            typeface = ResourcesCompat.getFont(context, R.font.roboto_bold)
            setPadding(16.dp, 16.dp, 16.dp, 8.dp)
        }
        root.addView(localizedTitle)

        // Жанры и год
        val genresYear = TextView(context).apply {
            text = "${film.genres.joinToString(", ")}, ${film.year} год"
            textSize = 16f
            setTextColor("#4B4B4B".toColorInt())
            typeface = ResourcesCompat.getFont(context, R.font.roboto_regular)
            setPadding(16.dp, 4.dp, 16.dp, 4.dp)
        }
        root.addView(genresYear)

        // Рейтинг с разными размерами и десятичным числом
        val ratingValue = film.rating.let { String.format("%.1f", it) } ?: "N/A"
        val ratingText = "$ratingValue КиноПоиск"
        val spannable = SpannableString(ratingText)

        // число рейтинга (24sp)
        spannable.setSpan(
            AbsoluteSizeSpan(24, true),
            0,
            ratingValue.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        // слово "КиноПоиск" (16sp)
        spannable.setSpan(
            AbsoluteSizeSpan(16, true),
            ratingValue.length + 1,
            ratingText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val rating = TextView(context).apply {
            text = spannable
            setTextColor("#0E3165".toColorInt())
            typeface = ResourcesCompat.getFont(context, R.font.roboto_bold)
            setPadding(16.dp, 4.dp, 16.dp, 4.dp)
        }
        root.addView(rating)

        // Описание
        val description = TextView(context).apply {
            text = film.description ?: "Описание отсутствует"
            textSize = 16f
            typeface = ResourcesCompat.getFont(context, R.font.roboto_regular)
            setPadding(16.dp, 8.dp, 16.dp, 16.dp)
        }
        root.addView(description)

        return scrollView
    }

    private val Int.dp: Int
        get() = (this * resources.displayMetrics.density).toInt()
}
