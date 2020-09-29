package com.pdm.serie1.bga.Activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.pdm.serie1.bga.Dtos.CategoriesMechanicsDto
import com.pdm.serie1.bga.R
import com.pdm.serie1.bga.Utils.Db.Entities.FavoriteGroups
import com.pdm.serie1.bga.Utils.Factory.FavoriteGroupViewModelFactory
import com.pdm.serie1.bga.Utils.FavOption
import com.pdm.serie1.bga.Utils.Models.FavoritesGroupViewModel
import com.pdm.serie1.bga.Utils.insertTextMessage
import kotlinx.android.synthetic.main.activity_favorite_group_creation.*
import java.util.*

class FavoritesGroupCreationActivity : AppCompatActivity() {

    private val editTextName by lazy { findViewById<EditText>(R.id.edit_text_favorite_group_name) }
    private val editTextPublisher by lazy { findViewById<EditText>(R.id.edit_text_publisher) }
    private val editTextDesigner by lazy { findViewById<EditText>(R.id.edit_text_designer) }

    private lateinit var factory: FavoriteGroupViewModelFactory

    private var checkedMechanics = BooleanArray(0)
    private var selectedMechanics = LinkedList<CategoriesMechanicsDto>()
    private var checkedCategories = BooleanArray(0)
    private var selectedCategories = LinkedList<CategoriesMechanicsDto>()

    private val model by lazy {
        ViewModelProviders.of(this, factory)[FavoritesGroupViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite_group_creation)
        setTitle(R.string.add_characteristics_group)

        factory = FavoriteGroupViewModelFactory(savedInstanceState)

        model.getCategories()
        model.getMechanics()


        button_categories.setOnClickListener {
            val categories = model.getCategories
            val categoriesNames = model.categoriesName
            if (checkedCategories.isEmpty())
                checkedCategories = BooleanArray(categories.size)
            createAlertDialog(
                "Choose one or more Categories!",
                categoriesNames,
                checkedCategories,
                categories,
                selectedCategories,
                category_scroll_view_linear_layout
            )
        }

        button_mechanics.setOnClickListener {
            val mechanics = model.getMechanics
            val mechanicsNames = model.mechanicsName
            if (checkedMechanics.isEmpty())
                checkedMechanics = BooleanArray(mechanics.size)
            createAlertDialog(
                "Choose one or more Mechanics!",
                mechanicsNames,
                checkedMechanics,
                mechanics,
                selectedMechanics,
                mechanics_scroll_view_linear_layout
            )
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInf: MenuInflater = menuInflater
        menuInf.inflate(R.menu.favorite_group_creation, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.done) {

            val name = editTextName.text.toString()
            val mechanic = selectedMechanics
            val category = selectedCategories
            val publisher = editTextPublisher.text.toString()
            val designer = editTextDesigner.text.toString()

            if (name.isEmpty())
                insertTextMessage(this, getString(R.string.please_insert_all))
            else {
                val url = model.searchFavoriteGames(
                    FavOption.FAVORITES,
                    1,
                    publisher,
                    designer,
                    mechanic.map { it.id }.toTypedArray(),
                    category.map { it.id }.toTypedArray()
                )
                val group = FavoriteGroups(name, url)

                model.insertFavoritesGroup(group).observe(this, Observer {
                    model.liveData.observeForever {
                        println("size = ${it.size}")
                        if (it.isNotEmpty())
                            it.forEach { game ->
                                model.insertGamesInFavoriteGroup(name, game)
                                game.artists!!.forEach {
                                    model.insertDeveloper(it, game.name!!)
                                }
                            }
                    }
                })
                finish()
            }
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun createAlertDialog(
        msg: String,
        itemsArray: Array<String>,
        searchArray: BooleanArray,
        dtosArray: Array<CategoriesMechanicsDto>,
        insertList: LinkedList<CategoriesMechanicsDto>,
        linearLayout: LinearLayout
    ) =
        AlertDialog
            .Builder(this)
            .setTitle(msg)
            .setMultiChoiceItems(
                itemsArray,
                searchArray
            ) { _: DialogInterface, idx: Int, checked: Boolean ->
                searchArray[idx] = checked
                if (checked)
                    insertList.add(dtosArray[idx])
                else
                    insertList.remove(dtosArray[idx])
            }
            .setPositiveButton("Done") { dialogInterface: DialogInterface, _: Int ->
                linearLayout.removeAllViews()
                insertList.forEach {
                    val textView = TextView(this)
                    textView.text = it.name
                    linearLayout.addView(textView)
                }
                dialogInterface.dismiss()
            }
            .show()
}