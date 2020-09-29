package com.pdm.serie1.bga.Utils.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pdm.serie1.bga.Activities.DetailsActivity
import com.pdm.serie1.bga.Activities.LIST
import com.pdm.serie1.bga.Activities.PopularGamesActivity
import com.pdm.serie1.bga.Activities.SearchActivity
import com.pdm.serie1.bga.Dtos.GamesDto
import com.pdm.serie1.bga.R
import com.pdm.serie1.bga.Utils.Db.Entities.Game
import com.pdm.serie1.bga.Utils.Db.Entities.dtoToDao
import com.pdm.serie1.bga.Utils.Models.AbstractModel
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

const val NAME = "NAME"
const val ORIGINAL_IMAGE = "ORIGINAL_IMAGE"
const val SMALL_IMAGE = "SMALL_IMAGE"
const val YEAR = "YEAR"
const val MIN_PLAYERS = "MIN_PLAYERS"
const val MAX_PLAYERS = "MAX_PLAYERS"
const val MIN_AGE = "MIN_AGE"
const val DESC = "DESC"
const val PRIMARY_PUBLISHER = "PRIMARY_PUBLISHER"
const val DEVELOPERS = "DEVELOPERS"
const val AVRG_RATING = "AVRG_RATING"
const val OFFICIAL_URL = "URL"
const val RATING_STAR = "RATING_STAR"
const val RULES = "RULES"

class GamesAdapter(
    private val model: AbstractModel,
    private val ctx: Context,
    private val groupName: String? = ""
) :
    RecyclerView.Adapter<GamesViewAdapter>() {
    companion object {
        val selectedGames = LinkedList<Game>()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamesViewAdapter {
        val gameView = LayoutInflater.from(parent.context)
            .inflate(R.layout.gamesview, parent, false) as LinearLayout

        return GamesViewAdapter(gameView)
    }

    override fun getItemCount(): Int =
        if (model.games.size == 31) model.games.size - 1
        else model.games.size

    override fun onBindViewHolder(holder: GamesViewAdapter, position: Int) {
        holder.bindTo(ctx, model.games[position], groupName)
    }
}

class GamesViewAdapter(private val gamesView: LinearLayout) : RecyclerView.ViewHolder(gamesView) {
    lateinit var game: GamesDto
    private lateinit var groupName: String
    private val gameName = gamesView.findViewById<TextView>(R.id.game_name_details)
    private val gameImage = gamesView.findViewById<ImageView>(R.id.game_image_details)
    private val gameYear = gamesView.findViewById<TextView>(R.id.game_year)
    private val gameRating = gamesView.findViewById<TextView>(R.id.game_rating_details)
    private val gameRatingBar = gamesView.findViewById<RatingBar>(R.id.ratingBar)


    fun bindTo(ctx: Context, game: GamesDto, groupName: String?) {
        this.game = game

        this.groupName = groupName ?: ""

        Glide.with(ctx).load(game.images.small).into(gameImage)

        val year = if (game.year_published == 0) "Undefined" else game.year_published.toString()
        val rate = roundUpRating(game.average_user_rating.toString())

        gameName.text = game.name
        gameYear.text = String.format("Year: $year")
        gameRating.text = String.format("Rating: $rate")
        gameRatingBar.rating = rate
    }

    init {
        gamesView.setOnClickListener {
            if (gamesView.context !is SearchActivity && find(gamesView)) {
                unselectList(it)
            } else {
                val intent = Intent(gamesView.context, DetailsActivity::class.java)
                initializeIntent(intent)
                gamesView.context.startActivity(intent)
            }
        }
        gamesView.setOnLongClickListener {
            if(gamesView.context is SearchActivity || gamesView.context is PopularGamesActivity)
                return@setOnLongClickListener false
            selectList(it)
            return@setOnLongClickListener true
        }
    }

    private fun find(it: LinearLayout): Boolean =
        GamesAdapter.selectedGames.find { game -> game.name.equals(((it[1] as LinearLayout)[0] as TextView).text.toString()) } != null

    private fun initializeIntent(intent: Intent) {
        val rate = roundUpRating(game.average_user_rating.toString())

        intent.putExtra(NAME, game.name)
        intent.putExtra(ORIGINAL_IMAGE, game.images.original)
        intent.putExtra(YEAR, game.year_published.toString())
        intent.putExtra(MIN_PLAYERS, game.min_players.toString())
        intent.putExtra(MAX_PLAYERS, game.max_players.toString())
        intent.putExtra(MIN_AGE, game.min_age.toString())
        intent.putExtra(DESC, game.description_preview)
        intent.putExtra(PRIMARY_PUBLISHER, game.primary_publisher)
        intent.putExtra(DEVELOPERS, game.artists)
        intent.putExtra(AVRG_RATING, rate)
        intent.putExtra(OFFICIAL_URL, game.official_url)
        intent.putExtra(RATING_STAR, rate)
        intent.putExtra(RULES, game.rules_url)
        intent.putExtra(SMALL_IMAGE, game.images.small)
        intent.putExtra(LIST, groupName)
    }

    private fun roundUpRating(rate: String) =
        BigDecimal(rate).setScale(1, RoundingMode.HALF_UP).toFloat()

    private fun selectList(it: View) {
        paintLinearLayout(it as LinearLayout, Color.DKGRAY, Color.WHITE)
        GamesAdapter.selectedGames.add(dtoToDao(game))
    }

    private fun unselectList(it: View) {
        paintLinearLayout(it as LinearLayout,Color.WHITE, Color.GRAY)
        GamesAdapter.selectedGames.remove(dtoToDao(game))
    }

    private fun paintLinearLayout(it: LinearLayout,backgroundColor: Int, textColor: Int){
        it.setBackgroundColor(backgroundColor)
        (it[0] as ImageView).setBackgroundColor(backgroundColor)
        var linear = it[1] as LinearLayout
        linear.setBackgroundColor(backgroundColor)
        for(i in 0..1){
            (linear[i] as TextView).setTextColor(textColor)
            linear[i].setBackgroundColor(backgroundColor)
        }
        linear = linear[2] as LinearLayout
        linear.setBackgroundColor(backgroundColor)
        val textView = linear[0] as TextView
        textView.setTextColor(textColor)
        textView.setBackgroundColor(backgroundColor)
        val ratingBar = linear[1] as RatingBar
        ratingBar.setBackgroundColor(backgroundColor)
    }
}