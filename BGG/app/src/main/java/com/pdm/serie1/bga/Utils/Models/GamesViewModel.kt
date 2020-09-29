package com.pdm.serie1.bga.Utils.Models

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.android.volley.NoConnectionError
import com.pdm.serie1.bga.Activities.TAG
import com.pdm.serie1.bga.Dtos.GamesDto
import com.pdm.serie1.bga.R
import com.pdm.serie1.bga.Utils.BGAApp
import com.pdm.serie1.bga.Utils.BGAWebApi
import com.pdm.serie1.bga.Utils.Option
import com.pdm.serie1.bga.Utils.Option.GAME
import com.pdm.serie1.bga.Utils.Option.MOST_POPULAR_GAMES

class GamesViewModel(private val bgaApi: BGAWebApi) : PagedAbstractModel(), Parcelable {


    private lateinit var current: String
    private var publisher:String=""
    private var artist: String=""
    private lateinit var option: Option
    private var searching = false

    fun searchForInfo(searchInfo: String = "", option: Option = GAME, page: Int) {
        if (searching) return
        searching = true
        current = searchInfo
        this.page = page
        this.option = option

        Log.v(TAG, "** Search Information from Board Game Atlas... **")
        bgaApi.searchForInfo(searchInfo, page, option, { searchDto ->
            Log.v(TAG, "** Completed Search Information from Board Game Atlas! **")
            liveData.value = searchDto.games
            if (searchDto.games.isEmpty())
                Toast.makeText(bgaApi.ctx, "Game $searchInfo not found!", Toast.LENGTH_SHORT).show()

            searching = false
        }, {
            if (it is NoConnectionError) {
                Toast.makeText(bgaApi.ctx, bgaApi.ctx.getString(R.string.no_internet), Toast.LENGTH_LONG).show()
            } else {
                throw it
            }
        })
    }



    fun searchForPopularGames(page: Int) {
        if (games.isNotEmpty() && page == this.page) return
        searchForInfo(option = MOST_POPULAR_GAMES, page = page)
    }


    constructor(parcel: Parcel) : this(BGAApp.BGAWeb) {
        val searchInfo = parcel.readString()!!
        val option = parcel.readValue(Option::class.java.classLoader) as Option
        val page = parcel.readInt()

        Log.v(TAG, "SearchInfo = $searchInfo and option = $option")
        searchForInfo(searchInfo, option, page)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(current)
        parcel.writeString(publisher)
        parcel.writeString(artist)
        parcel.writeValue(option)
        parcel.writeInt(page)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<GamesViewModel> {
        override fun createFromParcel(parcel: Parcel): GamesViewModel {
            return GamesViewModel(parcel)
        }

        override fun newArray(size: Int): Array<GamesViewModel?> {
            return arrayOfNulls(size)
        }
    }
}