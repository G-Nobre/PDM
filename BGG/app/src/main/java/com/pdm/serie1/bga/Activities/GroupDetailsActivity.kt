package com.pdm.serie1.bga.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pdm.serie1.bga.R
import com.pdm.serie1.bga.Utils.Adapters.GamesAdapter
import com.pdm.serie1.bga.Utils.BGAApp
import com.pdm.serie1.bga.Utils.Factory.GroupDetailsViewModelFactory
import com.pdm.serie1.bga.Utils.Models.GroupDetailsViewModel
import kotlinx.android.synthetic.main.activity_group_details.*

class GroupDetailsActivity : AppCompatActivity() {

    private lateinit var factory: GroupDetailsViewModelFactory

    private val model: GroupDetailsViewModel by lazy {
        ViewModelProviders.of(this, factory)[GroupDetailsViewModel::class.java]
    }
    private val recyclerView by lazy { findViewById<RecyclerView>(R.id.group_games_view) }

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_group_details)

        factory = GroupDetailsViewModelFactory(bundle)

        GamesAdapter.selectedGames.clear()

        val groupName = intent.getStringExtra(LIST)

        val adapter = GamesAdapter(model, this, groupName)

        val presentTotal = findViewById<TextView>(R.id.present_total)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        title = "${getString(R.string.group)} $groupName"
        model
            .getAllGamesInAGroup(groupName!!)
            .observe(this, Observer {
                presentTotal.text = it.size.toString()

                model.mapLiveDataValuesToDTO(it) {
                    var retArray = emptyArray<String>()
                    BGAApp.db.DeveloperGameJoinDAO().getDevelopersFromGame(it)
                        .observe(this, Observer {
                            retArray = it.toTypedArray()
                        })
                    retArray
                }

                adapter.notifyDataSetChanged()
            })

        create_game.setOnClickListener {
            val newIntent = Intent(this, SearchActivity::class.java)
            newIntent.putExtra(LIST, groupName)
            startActivity(newIntent)
        }

        delete_game.setOnClickListener {
            GamesAdapter.selectedGames.forEach { game ->
                model.deleteGameFromGroup(groupName, game)
                    .observe(this, Observer {
                        presentTotal.text = it.size.toString()
                        adapter.notifyDataSetChanged()
                    })
                model.getGamesInGroupByName(game.name).observe(this, Observer {
                    if (it.isEmpty())
                        model.deleteGameByName(game)
                })
            }
        }
    }
}
