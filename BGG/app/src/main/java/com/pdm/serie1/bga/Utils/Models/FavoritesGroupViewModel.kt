package com.pdm.serie1.bga.Utils.Models

import android.os.AsyncTask
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.NoConnectionError
import com.pdm.serie1.bga.Activities.TAG
import com.pdm.serie1.bga.Dtos.CategoriesMechanicsDto
import com.pdm.serie1.bga.Dtos.GamesDto
import com.pdm.serie1.bga.R
import com.pdm.serie1.bga.Utils.BGAApp
import com.pdm.serie1.bga.Utils.BGAWebApi
import com.pdm.serie1.bga.Utils.Db.Entities.*
import com.pdm.serie1.bga.Utils.FavOption
import com.pdm.serie1.bga.Utils.asyncTask

class FavoritesGroupViewModel(private val bgaApi: BGAWebApi) : PagedAbstractModel(), Parcelable {


    private lateinit var current: String
    private var publisher: String = ""
    private var artist: String = ""
    private lateinit var favOption: FavOption
    private var searching = false
    private var mechanics = emptyArray<String>()
    private var categories = emptyArray<String>()

    private val liveDataMechanics: MutableLiveData<Array<CategoriesMechanicsDto>> by lazy {
        MutableLiveData<Array<CategoriesMechanicsDto>>(emptyArray())
    }

    private val liveDataCategories: MutableLiveData<Array<CategoriesMechanicsDto>> by lazy {
        MutableLiveData<Array<CategoriesMechanicsDto>>(emptyArray())
    }

    val getMechanics get() = liveDataMechanics.value ?: emptyArray()
    val getCategories get() = liveDataCategories.value ?: emptyArray()
    lateinit var mechanicsName: Array<String>
    lateinit var categoriesName: Array<String>

    fun searchAllFavoriteGroups() =
        BGAApp.db.FavoriteGroupsDAO().getAllFavoriteGroups()

    fun insertFavoritesGroup(group: FavoriteGroups): LiveData<List<FavoriteGroups>> {
        asyncTask(
            {BGAApp.db.FavoriteGroupsDAO().insertFavoriteGroup(group)},
            {Log.v(TAG, "** Inserting... **")}
        )
        return searchAllFavoriteGroups()
    }

    fun getGamesCountForFavoriteGroup(name: String) =
        BGAApp.db.FavoriteGameJoinDAO().getGamesCountForGroup(name)

    fun insertGamesInFavoriteGroup(groupName: String, game: GamesDto) =
        asyncTask(
            {
                BGAApp.db.GamesDAO().insertGame(dtoToDao(game))
                BGAApp.db.FavoriteGameJoinDAO().insert(FavoriteGameJoin(groupName, game.name!!))
            },
            {})

    fun searchByName(query: String): LiveData<List<FavoriteGroups>> =
        BGAApp.db.FavoriteGroupsDAO().getFavoriteGroupsbyName("$query%")

    fun searchGamesForGroupName(name: String): LiveData<List<Game>> =
        BGAApp.db.FavoriteGameJoinDAO().getGamesFromFavoriteGroup(name)

    fun removeFavoriteGroup(name: String): LiveData<List<FavoriteGroups>> {
        asyncTask(
            { BGAApp.db.FavoriteGroupsDAO().deleteFavoriteGroup(FavoriteGroups(name)) },
            { Log.v(TAG, "** Deleting... **") }
        )
        return searchAllFavoriteGroups()
    }

    fun insertDeveloper(developer: String, gameName: String) =
        asyncTask({BGAApp.db.DeveloperGameJoinDAO().insert(DeveloperGameJoin(developer, gameName))},{})

    fun searchFavoriteGames(
        option: FavOption,
        page: Int,
        publisher: String,
        artist: String,
        mechanics: Array<String>,
        categories: Array<String>
    ): String {
        if (searching) return ""
        searching = true
        this.current = ""
        this.page = page
        this.favOption = option
        this.publisher = publisher
        this.artist = artist
        this.mechanics = mechanics
        this.categories = categories

        Log.v(TAG, "** Search Information from Board Game Atlas... **")
        return bgaApi.searchForFavGames(
            page,
            publisher,
            artist,
            mechanics,
            categories,
            option,
            { searchDto ->
                Log.v(TAG, "** Completed Search Information from Board Game Atlas! **")
                liveData.value = searchDto.games
                if (searchDto.games.isEmpty())
                    Toast.makeText(bgaApi.ctx, "No game found!", Toast.LENGTH_SHORT).show()
                searching = false
            },
            {
                if (it is NoConnectionError) {
                    Toast.makeText(bgaApi.ctx, "No Internet connection!", Toast.LENGTH_LONG).show()
                } else {
                    throw it
                }
            })
    }

    fun removeAllFavoriteGroups() {
        asyncTask(
            { BGAApp.db.FavoriteGroupsDAO().nukeFavoriteGroups() },
            { Toast.makeText(bgaApi.ctx, "All Lists Deleted!", Toast.LENGTH_SHORT).show() }
        )
    }

    constructor(parcel: Parcel) : this(BGAApp.BGAWeb)

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<FavoritesGroupViewModel> {
        override fun createFromParcel(parcel: Parcel): FavoritesGroupViewModel {
            return FavoritesGroupViewModel(parcel)
        }

        override fun newArray(size: Int): Array<FavoritesGroupViewModel?> {
            return arrayOfNulls(size)
        }
    }

    fun getMechanics() =
        bgaApi.getAllMechanics(
            FavOption.MECHANIC,
            {
                Log.v(TAG, "Mechanics data request completed!")
                liveDataMechanics.value = it.mechanics
                mechanicsName = it.mechanics.map { it.name }.toTypedArray()
            },
            {
                if (it is NoConnectionError) {
                    noConnectionMessage()
                } else
                    throw it
            })

    fun getCategories() =
        bgaApi.getAllCategories(
            FavOption.CATEGORY,
            {
                Log.v(TAG, "Categories data request completed!")
                liveDataCategories.value = it.categories
                categoriesName = it.categories.map { it.name }.toTypedArray()
            },
            {
                if (it is NoConnectionError) {
                    noConnectionMessage()
                } else
                    throw it
            })

    private fun noConnectionMessage() {
        Toast.makeText(
            bgaApi.ctx,
            bgaApi.ctx.getString(R.string.no_internet),
            Toast.LENGTH_SHORT
        ).show()
    }

}
