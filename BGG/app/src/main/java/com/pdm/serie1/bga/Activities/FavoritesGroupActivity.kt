package com.pdm.serie1.bga.Activities

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.work.WorkManager
import com.pdm.serie1.bga.R
import com.pdm.serie1.bga.Utils.*
import com.pdm.serie1.bga.Utils.Adapters.NAME
import com.pdm.serie1.bga.Utils.Factory.FavoriteGroupViewModelFactory
import com.pdm.serie1.bga.Utils.Models.FavoritesGroupViewModel
import kotlinx.android.synthetic.main.activity_favorite_groups.*
import java.util.*


class FavoritesGroupActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var factory: FavoriteGroupViewModelFactory

    private val linearLayout by lazy {
        findViewById<LinearLayout>(R.id.favorite_layout)
    }

    private val model: FavoritesGroupViewModel by lazy {
        ViewModelProviders.of(this, factory)[FavoritesGroupViewModel::class.java]
    }

    private var selected = false
    private val selectedFavoriteGroups = LinkedList<View>()
    private var urlMap = emptyMap<String,Pair<String,Int>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_groups)
        val delete_all = findViewById<Button>(R.id.clear_all_favorite_groups)
        selectedFavoriteGroups.clear()
        selected = false


        setTitle(R.string.favorite)

        factory = FavoriteGroupViewModelFactory(savedInstanceState)

        val scrollView = findViewById<ScrollView>(R.id.favorite_layout_scroll)
        resizeScrollViewHeight(windowManager, scrollView, resources)

        val searchView = findViewById<SearchView>(R.id.favorite_search)
        makeSearchViewClickable(this, searchView)

        searchAllFavoriteGroups()

        delete_all.setOnClickListener {
            if (!linearLayout.isEmpty())
                areYouSureDialog(
                    this,
                    selectedFavoriteGroups,
                    getString(R.string.delete_all_lists_confirmation)
                )
                { model.removeAllFavoriteGroups() }
        }
    }

    private fun searchAllFavoriteGroups() {
        model.searchAllFavoriteGroups().observe(this, Observer {
            linearLayout.removeAllViews()
            WorkManager.getInstance(BGAApp.BGAWeb.ctx).cancelAllWork()
            it.forEach { group ->
                model.getGamesCountForFavoriteGroup(group.FGName).observe(this, Observer {
                    scheduleBackgroundWork(group.FGName,group.url,it)
                })
                linearLayout.addView(textViewFactory(group.FGName))
            }

            if (linearLayout.isEmpty())
                clear_all_favorite_groups.visibility = View.INVISIBLE
            else
                clear_all_favorite_groups.visibility = View.VISIBLE
        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInf: MenuInflater = menuInflater
        menuInf.inflate(R.menu.favorite_groups, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.add) {
            val intent = Intent(this, FavoritesGroupCreationActivity::class.java)
            intent.putExtra("model", model)
            startActivity(intent)
            return true
        }
        if (id == R.id.delete) {
            if (selectedFavoriteGroups.isEmpty()) {
                Toast.makeText(
                    this,
                    "Select a group to delete",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            deleteSelectedLists(linearLayout, this, selectedFavoriteGroups, model)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onQueryTextSubmit(query: String?): Boolean = true

    override fun onQueryTextChange(newText: String): Boolean {
        model.searchByName(newText).observe(this, Observer {
            if (newText.isEmpty())
                searchAllFavoriteGroups()
            else {
                linearLayout.removeAllViews()
                it.forEach {
                    linearLayout.addView(textViewFactory(it.FGName))
                }
            }
        })
        return true
    }

    private fun textViewFactory(name: String): TextView {
        val text = TextView(this)
        text.text = name
        text.textSize = 24f
        text.setTextColor(Color.DKGRAY)
        text.setTypeface(null, Typeface.ITALIC)
        text.setOnClickListener {
            if (selected) {
                selected = unselectList(this, it, selectedFavoriteGroups)
            } else {
                val newIntent = Intent(this, FavoritesListGamesActivity::class.java)
                newIntent.putExtra(NAME, name)
                startActivity(newIntent)
            }
        }
        text.setOnLongClickListener {
            selected = selectList(it, selectedFavoriteGroups)
            return@setOnLongClickListener true
        }
        return text
    }


}

