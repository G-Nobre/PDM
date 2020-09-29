package com.pdm.serie1.bga.Activities

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.pdm.serie1.bga.R
import com.pdm.serie1.bga.Utils.BGAApp
import com.pdm.serie1.bga.Utils.Factory.GamesViewModelFactory
import com.pdm.serie1.bga.Utils.Models.GamesViewModel
import com.pdm.serie1.bga.Utils.getViewListener
import com.synnapps.carouselview.CarouselView

const val TAG = "BoardGameAtlas"

class MainActivity : AppCompatActivity() {

    private lateinit var factory: GamesViewModelFactory

    private val model: GamesViewModel by lazy {
        ViewModelProviders.of(this, factory)[GamesViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Board Game Atlas"

        factory = GamesViewModelFactory(savedInstanceState)

        model.searchForPopularGames(1)

        val searchBto = findViewById<Button>(R.id.search_button)
        searchBto.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        val popularGamesSearch = findViewById<Button>(R.id.group_games_view)
        popularGamesSearch.setOnClickListener {
            val intent = Intent(this, PopularGamesActivity::class.java)
            startActivity(intent)
        }

        val listsButton = findViewById<Button>(R.id.lists)
        listsButton.setOnClickListener {
            val intent = Intent(this, GroupActivity::class.java)
            startActivity(intent)
        }

        val favoritesButton = findViewById<Button>(R.id.favorite_lists)
        favoritesButton.setOnClickListener {
            val intent = Intent(this, FavoritesGroupActivity::class.java)
            startActivity(intent)
        }

        val carouselView = findViewById<CarouselView>(R.id.carousel)

        model.liveData.observe(this, Observer {
            if (it.isNotEmpty()) {
                carouselView.setViewListener(getViewListener(it, this))
                carouselView.pageCount = it.size
                carouselView.setIndicatorVisibility(View.INVISIBLE)
                println("intervalo antes ${carouselView.slideInterval}")
                carouselView.slideInterval = BGAApp.carouselSlideTimer
                println("intervalo depois ${carouselView.slideInterval}")
                carouselView.playCarousel()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.about) {
            return startIntent(this, AboutActivity::class.java)
        } else if (id == R.id.settings) {
            return startIntent(this, SettingsActivity::class.java)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun <T> startIntent(activity: Activity, clss: Class<T>): Boolean {
        val intent = Intent(activity, clss)
        activity.startActivity(intent)
        return true
    }


}
