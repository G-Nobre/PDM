package com.pdm.serie1.bga.Activities

import android.content.res.Configuration
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pdm.serie1.bga.R
import com.pdm.serie1.bga.Utils.Adapters.GamesAdapter
import com.pdm.serie1.bga.Utils.Adapters.NAME
import com.pdm.serie1.bga.Utils.BGAApp
import com.pdm.serie1.bga.Utils.Factory.FavoriteGroupViewModelFactory
import com.pdm.serie1.bga.Utils.Factory.GAMESVIEWMODEL
import com.pdm.serie1.bga.Utils.Models.FavoritesGroupViewModel
import com.pdm.serie1.bga.Utils.resizeRecyclerViewHeight

class FavoritesListGamesActivity : AppCompatActivity() {


    private lateinit var factory: FavoriteGroupViewModelFactory

    private val model: FavoritesGroupViewModel by lazy {
        ViewModelProviders.of(this, factory)[FavoritesGroupViewModel::class.java]
    }


    private val recyclerGameView by lazy { findViewById<RecyclerView>(R.id.group_games_view) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites_list_games)

        factory = FavoriteGroupViewModelFactory(savedInstanceState)

        title = getString(R.string.new_favorites_list)

        val adapter = GamesAdapter(model, this)
        val presentTotal = findViewById<TextView>(R.id.present_total_popular)

        recyclerGameView.adapter = adapter
        recyclerGameView.layoutManager = LinearLayoutManager(this)

        resizeRecyclerViewHeight(windowManager,resources,recyclerGameView)

        presentTotal.text = model.games.size.toString()

        val name = intent.getStringExtra(NAME)

        model.searchGamesForGroupName(name!!).observe(this, Observer {

            model.mapLiveDataValuesToDTO(it) {
                var retArray = emptyArray<String>()
                BGAApp.db.DeveloperGameJoinDAO().getDevelopersFromGame(it).observe(this, Observer {
                    retArray = it.toTypedArray()
                })
                println("retArray size is ${retArray.size}")
                retArray
            }

            presentTotal.text = if (it.size == 31) (it.size - 1).toString() else it.size.toString()
            adapter.notifyDataSetChanged()
        })
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (isChangingConfigurations) return

        Log.v(TAG, "** SAVING $TAG Model to a Bundle! **")
        outState.putParcelable(GAMESVIEWMODEL, model)
    }

    override fun onRestoreInstanceState(bundle: Bundle) {
        super.onRestoreInstanceState(bundle)
    }
}