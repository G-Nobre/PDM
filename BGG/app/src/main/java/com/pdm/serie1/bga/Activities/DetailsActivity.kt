package com.pdm.serie1.bga.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.pdm.serie1.bga.R
import com.pdm.serie1.bga.Utils.Adapters.*
import com.pdm.serie1.bga.Utils.Db.Entities.Game
import com.pdm.serie1.bga.Utils.Factory.GroupDetailsViewModelFactory
import com.pdm.serie1.bga.Utils.Models.GroupDetailsViewModel
import kotlinx.android.synthetic.main.activity_details.*

const val DEVELOPER_NAME = "DEVELOPER_NAME"

class DetailsActivity : AppCompatActivity() {

    private lateinit var factory: GroupDetailsViewModelFactory

    val model by lazy {
        ViewModelProviders.of(this, factory)[GroupDetailsViewModel::class.java]
    }

    private var developers = emptyArray<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        factory = GroupDetailsViewModelFactory(savedInstanceState)

        val gameName = findViewById<TextView>(R.id.game_name_details)
        val gameImage = findViewById<ImageView>(R.id.game_image_details)
        val gameYear = findViewById<TextView>(R.id.game_year_published_details)
        val gameMinP = findViewById<TextView>(R.id.game_min_players_details)
        val gameMaxP = findViewById<TextView>(R.id.game_max_players_details)
        val gameMinAge = findViewById<TextView>(R.id.game_min_age_details)
        val gameDesc = findViewById<TextView>(R.id.game_desc_details)
        val gamePrimaryPublisher = findViewById<TextView>(R.id.game_primary_publisher_details)
        val gameAvrgRating = findViewById<TextView>(R.id.details_game_rating)
        val gameRate = findViewById<RatingBar>(R.id.details_ratingBar)
        val gameDevelopers = findViewById<LinearLayout>(R.id.pulishers_table)
        val gameRules = findViewById<TextView>(R.id.game_rules)

        val url = intent.getStringExtra(OFFICIAL_URL)

        gameName.text = intent.getStringExtra(NAME)
        gameYear.text = intent.getStringExtra(YEAR)
        gameMinP.text = intent.getStringExtra(MIN_PLAYERS)
        gameMaxP.text = intent.getStringExtra(MAX_PLAYERS)
        gameMinAge.text = intent.getStringExtra(MIN_AGE)
        gamePrimaryPublisher.text = intent.getStringExtra(PRIMARY_PUBLISHER)
        gameDesc.text = intent.getStringExtra(DESC)
        gameAvrgRating.text = intent.getFloatExtra(AVRG_RATING, 0f).toString()
        gameRate.rating = intent.getFloatExtra(RATING_STAR, 0f)
        gameRules.text = intent.getStringExtra(RULES)

        title = "${gameName.text} Details"

        Glide.with(this)
            .load(intent.getStringExtra(ORIGINAL_IMAGE))
            .into(gameImage)

        gameImage.setOnClickListener {
            createBrowserActivity(url!!)
        }

        gameRules.setOnClickListener {
            createBrowserActivity(gameRules.text.toString())
        }

        gamePrimaryPublisher.setOnClickListener {
            createSearchActivity(PRIMARY_PUBLISHER, gamePrimaryPublisher.text.toString())
        }

        developers = intent.getStringArrayExtra(DEVELOPERS)?: emptyArray()

        if(developers.isNotEmpty()) {
            developers.forEach { developer ->
                addDeveloperToView(developer, gameDevelopers)
            }
        } else {
            model.getDevelopers(gameName.text.toString()).observe(this, Observer {
                it.forEach {
                    addDeveloperToView(it,gameDevelopers)
                }
            })
        }

        add_to_list_button.setOnClickListener {
            val groupName = intent.getStringExtra(LIST)
            val game = Game(
                name = gameName.text.toString(),
                year = gameYear.text.toString().toInt(),
                minPlayers = gameMinP.text.toString().toInt(),
                maxPlayers = gameMaxP.text.toString().toInt(),
                minAge = gameMinAge.text.toString().toInt(),
                publisher = gamePrimaryPublisher.text.toString(),
                rating = gameRate.rating.toString().toFloat(),
                rules = gameRules.text.toString(),
                description = gameDesc.text.toString(),
                imageSmallUri = intent.getStringExtra(SMALL_IMAGE)!!,
                imageOriginalUri = intent.getStringExtra(ORIGINAL_IMAGE)!!,
                url = url?:""
            )
            val newIntent: Intent
            if (!groupName.isNullOrEmpty()) {
                model
                    .insertGameInGroup(groupName, game)
                    .observe(this, Observer {
                        developers.forEach {
                            model.insertDevelopers(it,game.name)
                        }
                        model.mapLiveDataValuesToDTO(it) {developers}
                    })
                newIntent = Intent(this, GroupDetailsActivity::class.java)
                newIntent.putExtra(LIST, groupName)
            } else {
                newIntent = Intent(this, ListsSelectActivity::class.java)
                newIntent.putExtra(DEVELOPERS,developers)
                newIntent.putExtras(intent)
            }
            startActivity(newIntent)
        }
    }

    private fun addDeveloperToView(
        developer: String,
        gameDevelopers: LinearLayout
    ) {
        val text = TextView(this)
        text.text = developer
        gameDevelopers.addView(text)

        text.setOnClickListener {
            createSearchActivity(DEVELOPER_NAME, developer)
        }
    }

    private fun createBrowserActivity(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    private fun createSearchActivity(spinnerOption: String, content: String) {
        val intent = Intent(this, SearchActivity::class.java)
        intent.putExtra(spinnerOption, content)
        startActivity(intent)
    }
}
