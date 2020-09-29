package com.pdm.serie1.bga.Utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.forEach
import com.bumptech.glide.Glide
import com.pdm.serie1.bga.Activities.*
import com.pdm.serie1.bga.Dtos.GamesDto
import com.pdm.serie1.bga.R
import com.pdm.serie1.bga.Utils.Adapters.*
import com.pdm.serie1.bga.Utils.Models.*
import com.synnapps.carouselview.ViewListener
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

fun resizeRecyclerViewHeight(windowManager: WindowManager, resources: Resources, view: View) {
    val metrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(metrics)

    val percentage =
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 7.5 else 4.7

    view.layoutParams.height = (metrics.heightPixels / 10 * percentage).toInt()
}

fun makeSearchBarClickable(listener: SearchView.OnQueryTextListener, searchView: SearchView) {
    searchView.setBackgroundColor(Color.LTGRAY)
    searchView.isIconified = false
    searchView.clearFocus()
    searchView.setOnQueryTextListener(listener)
}

fun pagingSetOnClickListener(
    activity: Activity,
    prevSearch: Button,
    nextSearch: Button,
    currentPage: Button,
    model: PagedAbstractModel,
    spinner: Spinner = Spinner(activity),
    searchView: SearchView = SearchView(activity)
) {
    prevSearch.setOnClickListener {
        if (model.page < 1) model.page = 2
        currentPage.text =
            updateRecyclerViewData(model.page - 1, model, activity, searchView, spinner = spinner)
        if (model.page <= 1) {
            prevSearch.visibility = View.INVISIBLE
            if (model.games.size == 31)
                nextSearch.visibility = View.VISIBLE
        }
    }
    nextSearch.setOnClickListener {
        currentPage.text =
            updateRecyclerViewData(model.page + 1, model, activity, searchView, spinner = spinner)
        prevSearch.visibility = View.VISIBLE
        if (model.games.size < 31)
            nextSearch.visibility = View.INVISIBLE
    }
}

fun updateRecyclerViewData(
    page: Int = 1,
    model: PagedAbstractModel,
    activity: Activity,
    searchView: SearchView = SearchView(activity),
    spinner: Spinner = Spinner(activity)
): String {
    if (model.page == page && model.games.isNotEmpty()) return ""

    if (activity is SearchActivity) {
        val name: String = searchView.query.toString()
        activity.title = "\"$name\""
        (model as GamesViewModel).searchForInfo(
            name,
            getOption(spinner.selectedItem.toString()),
            page
        )
    } else if (activity is PopularGamesActivity)
        (model as GamesViewModel).searchForPopularGames(page)

    return model.page.toString()
}


private fun getOption(item: String) = Option.valueOf(item.toUpperCase())

fun hideKeyboard(activity: Activity, window: Window) {
    val inputManager =
        (activity.getSystemService(Context.INPUT_METHOD_SERVICE)) as InputMethodManager
    inputManager.hideSoftInputFromWindow(
        activity.currentFocus?.windowToken,
        InputMethodManager.HIDE_NOT_ALWAYS
    )
    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
}

fun resizeScrollViewHeight(
    windowManager: WindowManager,
    layoutScroll: ScrollView,
    resources: Resources
) {
    val metrics = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(metrics)

    val percentage =
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 6 else 4

    layoutScroll.layoutParams.height = (metrics.heightPixels / 10 * percentage)
}


fun getImage(activity: Activity, gamesArray: Array<GamesDto>, idx: Int): ImageView {
    val imageView = ImageView(activity)
    Glide
        .with(activity)
        .load(gamesArray[idx].images.medium)
        .into(imageView)

    /**
     * RESIZING IMAGE TO AN ACCEPTABLE SIZE
     */
    val metrics = DisplayMetrics()
    activity.windowManager.defaultDisplay.getMetrics(metrics)
    val layoutParams = ViewGroup.LayoutParams(300, 250)
    imageView.layoutParams = layoutParams
    return imageView
}

fun getViewListener(gamesArray: Array<GamesDto>, activity: Activity) =
    ViewListener {
        val idx = IntRange(0, gamesArray.size - 1).random()
        val view = LinearLayout(activity)
        view.orientation = LinearLayout.HORIZONTAL

        /**
         * GAME IMAGE
         */
        val imageView = getImage(activity, gamesArray, idx)

        /**
         * GETTING ARRAYS WITH CORRECT IDX AND VALUES
         */
        val fieldsArray = makeStringArrayForGameDtoFields(activity)
        val valuesArray = makeStringArrayForGameDtoValues(gamesArray, idx)

        val verticalLayout = LinearLayout(activity)
        verticalLayout.orientation = LinearLayout.VERTICAL
        /**
         * CREATING THE VERTICAL LAYOUT
         */
        var i = 0
        var inc = 1
        while (i < fieldsArray.size) {
            val horizontalRow = LinearLayout(activity)
            for (index in i until i + inc) {
                val stringField = fieldsArray[index]
                val stringValue = valuesArray[index]
                val horizontal =
                    makeHorizontalLayoutForValues(activity, stringField, " $stringValue")
                addLeftMarginToLinearLayout(horizontal, 20)
                horizontalRow.addView(horizontal)
            }
            if (i >= 1) {
                inc = 2
                if (i > 1)
                    i++
            }
            verticalLayout.addView(horizontalRow)
            i++
        }

        view.addView(imageView)
        view.addView(verticalLayout)
        view.setOnClickListener {
            val intent = Intent(activity, DetailsActivity::class.java)
            initializeIntent(intent, gamesArray[idx])
            activity.startActivity(intent)
        }

        view
    }

private fun initializeIntent(intent: Intent, game: GamesDto) {
    val rate = roundUpRating(game.average_user_rating.toString())

    intent.putExtra(NAME, game.name)
    intent.putExtra(ORIGINAL_IMAGE, game.images.original)
    intent.putExtra(YEAR, game.year_published.toString())
    intent.putExtra(MIN_PLAYERS, game.min_players.toString())
    intent.putExtra(MAX_PLAYERS, game.max_players.toString())
    intent.putExtra(MIN_AGE, game.min_age.toString())
    intent.putExtra(DESC, game.description_preview)
    intent.putExtra(PRIMARY_PUBLISHER, game.primary_publisher)
    intent.putExtra(DEVELOPERS, game.artists)
    intent.putExtra(AVRG_RATING, rate)
    intent.putExtra(OFFICIAL_URL, game.official_url)
    intent.putExtra(RATING_STAR, rate)
    intent.putExtra(RULES, game.rules_url)
    intent.putExtra(SMALL_IMAGE, game.images.small)
}

private fun roundUpRating(rate: String) =
    BigDecimal(rate).setScale(1, RoundingMode.HALF_UP).toFloat()

private fun addLeftMarginToLinearLayout(horizontal: LinearLayout, value: Int) {
    val layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )
    layoutParams.setMargins(value, 0, 0, 0)
    horizontal.layoutParams = layoutParams
}

private fun makeStringArrayForGameDtoValues(gamesArray: Array<GamesDto>, idx: Int) = arrayOf(
    gamesArray[idx].name!!,
    gamesArray[idx].primary_publisher!!,
    gamesArray[idx].year_published.toString(),
    gamesArray[idx].min_age.toString(),
    gamesArray[idx].min_players.toString(),
    gamesArray[idx].max_players.toString()
)

private fun makeStringArrayForGameDtoFields(activity: Activity) = arrayOf(
    activity.getString(R.string.game_name_txt),
    activity.getString(R.string.game_primary_publisher),
    activity.getString(R.string.game_year),
    activity.getString(R.string.game_min_age),
    activity.getString(R.string.game_min_players),
    activity.getString(R.string.game_max_players)
)

private fun makeHorizontalLayoutForValues(
    activity: Activity,
    stringValue: String,
    value: String
): LinearLayout {
    val linearLayout = LinearLayout(activity)

    val stringValueText = TextView(activity)
    stringValueText.text = stringValue
    stringValueText.setTextColor(Color.BLACK)

    val valueText = TextView(activity)
    valueText.text = value
    valueText.setTextColor(Color.BLACK)

    linearLayout.addView(stringValueText)
    linearLayout.addView(valueText)
    return linearLayout
}

fun makeSearchViewClickable(listener: SearchView.OnQueryTextListener, searchView: SearchView) {
    searchView.setBackgroundColor(Color.LTGRAY)
    searchView.isIconified = false
    searchView.clearFocus()
    searchView.setOnQueryTextListener(listener)
}

fun deleteSelectedLists(
    view: View,
    activity: Activity = Activity(),
    viewsList: LinkedList<View>,
    model: AbstractModel
): Boolean {
    if (model is FavoritesGroupViewModel) {
        areYouSureDialog(
            activity,
            viewsList,
            activity.getString(R.string.remove_selected_lists_confirmation)
        ) {
            viewsList.forEach {
                model.removeFavoriteGroup((it as TextView).text.toString())
            }
        }
        (view as LinearLayout).forEach { println((it as TextView).text) }
    } else if (activity is GroupActivity) {
        areYouSureDialog(
            activity,
            viewsList,
            activity.getString(R.string.remove_selected_lists_confirmation)
        ) {
            viewsList.forEach {
                (model as GroupViewModel).removeGameList((it as TextView).text.toString())
            }
        }
    }
    return true
}


fun selectList(it: View, viewsList: LinkedList<View>): Boolean {
    it.setBackgroundColor(Color.DKGRAY)
    (it as TextView).setTextColor(Color.WHITE)
    viewsList.add(it)
    return true
}

fun hidePagingButtons(currPage: Button, nextSearch: Button, prevSearch: Button) {
    currPage.visibility = View.INVISIBLE
    nextSearch.visibility = View.INVISIBLE
    prevSearch.visibility = View.INVISIBLE
}

fun unselectList(
    activity: Activity = Activity(),
    textView: View,
    selectedGroups: LinkedList<View>
): Boolean {
    textView.setBackgroundColor(Color.WHITE)
    (textView as TextView).setTextColor(Color.DKGRAY)
    selectedGroups.remove(textView)
    if (selectedGroups.isEmpty()) {
        return false
    }
    return true
}

fun areYouSureDialog(
    activity: Activity = Activity(),
    viewsList: LinkedList<View>,
    message: String,
    method: () -> (Unit)
) {
    if (activity is GroupActivity)
        AlertDialog
            .Builder(activity)
            .setMessage(message)
            .setPositiveButton(R.string.yes_button) { _, _ ->
                method()
                viewsList.clear()
            }
            .setNegativeButton(R.string.no_button) { _, _ ->
                Toast.makeText(
                    activity,
                    "Deletion Canceled.",
                    Toast.LENGTH_SHORT
                ).show()
            }.show()
    else if (activity is FavoritesGroupActivity)
        AlertDialog
            .Builder(activity)
            .setMessage(message)
            .setPositiveButton(R.string.yes_button) { _, _ ->
                method()
                viewsList.clear()
            }
            .setNegativeButton(R.string.no_button) { _, _ ->
                Toast.makeText(
                    activity,
                    "Deletion Canceled.",
                    Toast.LENGTH_SHORT
                ).show()
            }.show()
}

fun insertTextMessage(activity: Activity, text: String) {
    Toast.makeText(
        activity,
        text,
        Toast.LENGTH_SHORT
    ).show()
}

fun hideKeyboard(activity: Activity) {
    val inputManager =
        (activity.getSystemService(Context.INPUT_METHOD_SERVICE)) as InputMethodManager
    inputManager.hideSoftInputFromWindow(
        activity.currentFocus?.windowToken,
        InputMethodManager.HIDE_NOT_ALWAYS
    )
}