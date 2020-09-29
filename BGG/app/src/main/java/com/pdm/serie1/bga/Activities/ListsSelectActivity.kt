package com.pdm.serie1.bga.Activities

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.pdm.serie1.bga.R
import com.pdm.serie1.bga.Utils.Adapters.*
import com.pdm.serie1.bga.Utils.Db.Entities.Game
import com.pdm.serie1.bga.Utils.Factory.GroupDetailsViewModelFactory
import com.pdm.serie1.bga.Utils.Factory.GroupViewModelFactory
import com.pdm.serie1.bga.Utils.Models.GroupDetailsViewModel
import com.pdm.serie1.bga.Utils.Models.GroupViewModel
import kotlinx.android.synthetic.main.activity_lists_select.*
import java.util.*

class ListsSelectActivity : AppCompatActivity() {

    private var selectedLists = LinkedList<TextView>()

    private lateinit var gameFactory: GroupDetailsViewModelFactory
    private lateinit var factory: GroupViewModelFactory

    private val model by lazy { ViewModelProviders.of(this, factory)[GroupViewModel::class.java] }
    private val gameModel by lazy {
        ViewModelProviders.of(
            this,
            gameFactory
        )[GroupDetailsViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lists_select)

        title = getString(R.string.list_selection)

        val game = Game(
            name = intent.getStringExtra(NAME) ?: "",
            year = intent.getStringExtra(YEAR)?.toInt() ?: 0,
            minPlayers = intent.getStringExtra(MIN_PLAYERS)?.toInt() ?: 0,
            maxPlayers = intent.getStringExtra(MAX_PLAYERS)?.toInt() ?: 0,
            minAge = intent.getStringExtra(MIN_AGE)?.toInt() ?: 0,
            publisher = intent.getStringExtra(PRIMARY_PUBLISHER) ?: "",
            description = intent.getStringExtra(DESC) ?: "",
            rating = intent.getFloatExtra(RATING_STAR, 0f),
            rules = intent.getStringExtra(RULES) ?: "",
            imageOriginalUri = intent.getStringExtra(ORIGINAL_IMAGE) ?: "",
            imageSmallUri = intent.getStringExtra(SMALL_IMAGE) ?: "",
            url = intent.getStringExtra(OFFICIAL_URL) ?: ""
        )

        val developers = intent.getStringArrayExtra(DEVELOPERS)

        factory = GroupViewModelFactory(savedInstanceState)
        gameFactory = GroupDetailsViewModelFactory(savedInstanceState)

        model.searchAllGameLists().observe(this, Observer {
            it.forEach { all_lists_layout.addView(textViewFactory(it.GName)) }
        })

        add_selected_lists_button.setOnClickListener {
            if (selectedLists.isEmpty())
                Toast.makeText(
                    this,
                    getString(R.string.no_lists_selected),
                    Toast.LENGTH_SHORT
                ).show()
            else {
                selectedLists.forEach {
                    gameModel.insertGameInGroup(it.text.toString(), game)
                }
                developers!!.forEach { gameModel.insertDevelopers(it, game.name) }

                val intent = Intent(this, GroupActivity::class.java)
                startActivity(intent)
            }
        }


    }

    private fun textViewFactory(name: String): TextView {
        val text = TextView(this)
        text.text = name
        text.textSize = 24f
        text.setTextColor(Color.DKGRAY)
        text.setTypeface(null, Typeface.ITALIC)
        text.setOnClickListener {
            selectList(it)
        }
        text.setOnLongClickListener {
            unselectList(it)
            return@setOnLongClickListener true
        }
        return text
    }

    private fun selectList(it: View) {
        it.setBackgroundColor(Color.DKGRAY)
        (it as TextView).setTextColor(Color.WHITE)
        selectedLists.add(it)
    }

    private fun unselectList(it: View) {
        it.setBackgroundColor(Color.WHITE)
        (it as TextView).setTextColor(Color.DKGRAY)
        selectedLists.remove(it)
    }
}
