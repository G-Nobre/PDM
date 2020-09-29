package com.pdm.serie1.bga.Activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pdm.serie1.bga.R
import com.pdm.serie1.bga.Utils.Adapters.GamesAdapter
import com.pdm.serie1.bga.Utils.Factory.GAMESVIEWMODEL
import com.pdm.serie1.bga.Utils.Factory.GamesViewModelFactory
import com.pdm.serie1.bga.Utils.Models.GamesViewModel
import com.pdm.serie1.bga.Utils.pagingSetOnClickListener
import com.pdm.serie1.bga.Utils.resizeRecyclerViewHeight

class PopularGamesActivity : AppCompatActivity() {

    private lateinit var factory: GamesViewModelFactory

    private val model: GamesViewModel by lazy {
        ViewModelProviders.of(this, factory)[GamesViewModel::class.java]
    }

    private val currPage by lazy {
        findViewById<Button>(R.id.currrentPage)
    }

    private val recyclerGameView by lazy { findViewById<RecyclerView>(R.id.group_games_view) }
    private val prev by lazy { findViewById<Button>(R.id.prev) }
    private val next by lazy { findViewById<Button>(R.id.next) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_popular_games)

        factory = GamesViewModelFactory(savedInstanceState)

        setTitle(R.string.most_popular_games)

        val adapter = GamesAdapter(model, this)
        val presentTotal = findViewById<TextView>(R.id.present_total_popular)

        if (model.page == 1)
            prev.visibility = View.INVISIBLE

        recyclerGameView.adapter = adapter
        recyclerGameView.layoutManager = LinearLayoutManager(this)

        resizeRecyclerViewHeight(windowManager, resources, recyclerGameView)

        pagingSetOnClickListener(this, prev, next, currentPage = currPage, model = model)

        presentTotal.text = model.games.size.toString()

        model.observe(this) {
            adapter.notifyDataSetChanged()
            presentTotal.text = if (it.size == 31) (it.size - 1).toString() else it.size.toString()
        }

        model.searchForPopularGames(model.page)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (isChangingConfigurations) return

        Log.v(TAG, "** SAVING $TAG Model to a Bundle! **")
        outState.putParcelable(GAMESVIEWMODEL, model)
    }

    override fun onRestoreInstanceState(bundle: Bundle) {
        super.onRestoreInstanceState(bundle)
        currPage.text = model.page.toString()
    }
}
