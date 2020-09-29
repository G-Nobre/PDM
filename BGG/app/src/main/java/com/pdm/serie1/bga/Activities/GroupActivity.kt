package com.pdm.serie1.bga.Activities

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.core.view.isEmpty
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.pdm.serie1.bga.R
import com.pdm.serie1.bga.Utils.*
import com.pdm.serie1.bga.Utils.Db.Entities.Group
import com.pdm.serie1.bga.Utils.Factory.GroupViewModelFactory
import com.pdm.serie1.bga.Utils.Models.GroupViewModel
import kotlinx.android.synthetic.main.activity_group.*
import kotlinx.android.synthetic.main.search_view.*
import java.util.*

const val LIST = "LIST"

class GroupActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private lateinit var factory: GroupViewModelFactory

    private val model: GroupViewModel by lazy {
        ViewModelProviders.of(this, factory)[GroupViewModel::class.java]
    }
    private val linearLayout by lazy {
        findViewById<LinearLayout>(R.id.lists_layout)
    }

    private val selectedLists = LinkedList<View>()
    private var selected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)
        savedInstanceState?.clear()

        setTitle(R.string.personal_game_lists)

        factory = GroupViewModelFactory(savedInstanceState)


        val editText = findViewById<EditText>(R.id.edit_text_lists)
        val createButton = findViewById<Button>(R.id.create)
        val deleteButton = findViewById<Button>(R.id.delete)


        createButton.setOnClickListener {
            val listName = editText.text.toString()
            if (listName.isEmpty())
                insertTextMessage(this,"Please insert the name of the list")
            else {
                model.insertGameList(listName)
                if (!clear_all.isVisible)
                    clear_all.visibility = View.VISIBLE
                editText.text.clear()
            }
            hideKeyboard(this)
        }

        deleteButton.setOnClickListener {
            val listName = editText.text.toString()
            if (selectedLists.isEmpty()) {
                if (listName.isEmpty())
                    insertTextMessage(this, "Please insert the name of the list")
                else {
                    deleteNamedList(listName, editText)
                }
            }
            else {
                deleteSelectedLists(linearLayout,this,selectedLists,model)
            }
        }


        clear_all.setOnClickListener {
            areYouSureDialog(getString(R.string.delete_all_lists_confirmation))
            { model.removeAllGameLists() }
        }


        val scrollView = findViewById<ScrollView>(R.id.layout_scroll)
        resizeScrollViewHeight(windowManager,scrollView,resources)

        val searchView = findViewById<SearchView>(R.id.search_list)

        makeSearchViewClickable(this,searchView)

        searchAllGameLists()

    }

    private fun deleteNamedList(listName: String, editText: EditText) {
        if (!find(listName)) {
            insertTextMessage(this,"List $listName not found!")
        } else {
            areYouSureDialog("${getString(R.string.delete_list)} \'$listName\'?") {
                model.removeGameList(listName)
            }
            if (linearLayout.isEmpty())
                clear_all.visibility = View.INVISIBLE
            editText.text.clear()
        }
        hideKeyboard(this)
    }

    private fun searchAllGameLists() {
        model.searchAllGameLists().observe(this, Observer {
            linearLayout.removeAllViews()
            it.forEach {
                linearLayout.addView(textViewFactory(it.GName))
            }
            if (linearLayout.isEmpty())
                clear_all.visibility = View.INVISIBLE
        })
    }


    override fun onQueryTextSubmit(query: String): Boolean = true

    private fun updateListsLayout(query: String): LiveData<List<Group>> =
        model.searchByName(query)

    private fun textViewFactory(name: String): TextView {
        val text = TextView(this)
        text.text = name
        text.textSize = 24f
        text.setTextColor(Color.DKGRAY)
        text.setTypeface(null, Typeface.ITALIC)
        text.setOnClickListener {
            if (selected) {
                selected = unselectList(this,it,selectedLists)
            } else {
                val intent = Intent(this, GroupDetailsActivity::class.java)
                intent.putExtra(LIST,name)
                startActivity(intent)
            }
        }
        text.setOnLongClickListener {
            selected = selectList(it,selectedLists)
            return@setOnLongClickListener true
        }
        return text
    }

    override fun onQueryTextChange(newText: String): Boolean {
        updateListsLayout(newText).observe(this, Observer {
            if (newText.isEmpty())
                searchAllGameLists()
            else {
                linearLayout.removeAllViews()
                it.forEach {
                    linearLayout.addView(textViewFactory(it.GName))
                }
            }
        })
        return true
    }

    private fun find(name: String): Boolean {
        linearLayout.forEach { if ((it as TextView).text == name) return true }
        return false
    }

    private fun areYouSureDialog(message: String, method: () -> (Unit)) = AlertDialog
        .Builder(this)
        .setMessage(message)
        .setPositiveButton(R.string.yes_button) { _, _ ->
            method()
            selectedLists.clear()
        }
        .setNegativeButton(R.string.no_button) { _, _ ->
            insertTextMessage(this,getString(R.string.deletion_canceled))
        }.show()


}

