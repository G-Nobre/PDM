package com.pdm.serie1.bga.Activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pdm.serie1.bga.R
import com.pdm.serie1.bga.Utils.*
import com.pdm.serie1.bga.Utils.Adapters.GamesAdapter
import com.pdm.serie1.bga.Utils.Adapters.PRIMARY_PUBLISHER
import com.pdm.serie1.bga.Utils.Factory.GAMESVIEWMODEL
import com.pdm.serie1.bga.Utils.Factory.GamesViewModelFactory
import com.pdm.serie1.bga.Utils.Models.GamesViewModel


class SearchActivity : AppCompatActivity(), SearchView.OnQueryTextListener, View.OnClickListener {

    private lateinit var factory: GamesViewModelFactory

    private val model: GamesViewModel by lazy {
        ViewModelProviders.of(this, factory)[GamesViewModel::class.java]
    }

    private val searchView by lazy {
        findViewById<SearchView>(R.id.search_view)
    }

    private val currPage by lazy {
        findViewById<Button>(R.id.currrentPage_search)
    }

    private val recyclerGameView by lazy { findViewById<RecyclerView>(R.id.recyclerGames) }

    private val prevSearch by lazy { findViewById<Button>(R.id.prev_search) }

    private val nextSearch by lazy { findViewById<Button>(R.id.next_search) }

    private val spinner by lazy { findViewById<Spinner>(R.id.spinner) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_view)
        factory = GamesViewModelFactory(savedInstanceState)


        val adapter = GamesAdapter(model, this, intent.getStringExtra(LIST))

        val presentTotal = findViewById<TextView>(R.id.present_total)

        recyclerGameView.adapter = adapter
        recyclerGameView.layoutManager = LinearLayoutManager(this)

        resizeRecyclerViewHeight(windowManager, resources, recyclerGameView)

        presentTotal.text = model.games.size.toString()

        makeSearchBarClickable(this, searchView)

        createSpinner()

        pagingSetOnClickListener(this, prevSearch, nextSearch, currPage, model, spinner,searchView)

        requestBySpinnerOptionIfChanged()

        model.observe(this) {
            hideKeyboard(this, window)
            adapter.notifyDataSetChanged()
            presentTotal.text = if (it.size == 31) (it.size - 1).toString() else it.size.toString()
            if (presentTotal.text == "0")
                hidePagingButtons(currPage, nextSearch, prevSearch)
            else {
                changePagingButtonsVisibility()
            }

        }
    }

    private fun changePagingButtonsVisibility() {
        if (!currPage.isVisible)
            currPage.visibility = View.VISIBLE
        if (model.page == 1) {
            prevSearch.visibility = View.INVISIBLE
        }
        if (model.games.size < 31)
            nextSearch.visibility = View.INVISIBLE
        else
            if (nextSearch.visibility == View.INVISIBLE)
                nextSearch.visibility = View.VISIBLE
    }

    private fun requestBySpinnerOptionIfChanged() {
        val developer: String? = intent.getStringExtra(DEVELOPER_NAME)
        val company: String? = intent.getStringExtra(PRIMARY_PUBLISHER)

        if (model.games.isEmpty()) {
            hidePagingButtons(currPage, nextSearch, prevSearch)
            if (developer != null) {
                spinner.setSelection(INDEX_OF_DEVELOPER)
                searchView.setQuery(developer, true)
            } else if (company != null) {
                spinner.setSelection(INDEX_OF_COMPANY)
                searchView.setQuery(company, true)
            }
        }
    }

    private fun createSpinner() {
        ArrayAdapter.createFromResource(
            this,
            R.array.spinner_options,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = it
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        updateRecyclerViewData(model.page, model, this, searchView, spinner)
        currPage.visibility = View.VISIBLE
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean = false

    override fun onClick(p0: View?) {
        searchView.isIconified = false
    }

    override fun onResume() {
        super.onResume()
        searchView.clearFocus()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (isChangingConfigurations) return

        Log.v(TAG, " ** Saving $TAG Model to a Bundle! ** ")
        outState.putParcelable(GAMESVIEWMODEL, model)
    }


}
