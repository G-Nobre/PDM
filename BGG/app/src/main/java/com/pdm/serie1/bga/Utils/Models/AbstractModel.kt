package com.pdm.serie1.bga.Utils.Models

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.pdm.serie1.bga.Dtos.GamesDto
import com.pdm.serie1.bga.Dtos.ImageDto
import com.pdm.serie1.bga.Utils.BGAApp
import com.pdm.serie1.bga.Utils.BGAWebApi
import com.pdm.serie1.bga.Utils.Db.Entities.Game

abstract class AbstractModel : ViewModel() {
    val liveData: MutableLiveData<Array<GamesDto>> by lazy {
        MutableLiveData<Array<GamesDto>>(emptyArray())
    }

    val games: Array<GamesDto>
        get() = liveData.value ?: emptyArray()

    fun observe(owner: LifecycleOwner, observer: (Array<GamesDto>) -> Unit) {
        liveData.observe(owner, Observer {
            observer(it)
        })
    }
    fun mapLiveDataValuesToDTO(games: List<Game>,method:(name:String) -> Array<String>) {
        liveData.value = games.map { daoToDto(it,method(it.name)) }.toTypedArray()
    }

    private fun daoToDto(game: Game, developers:Array<String> = emptyArray()): GamesDto =
        GamesDto(
            ImageDto(game.imageOriginalUri!!, game.imageSmallUri!!,game.imageMediumUri!!),
            game.name,
            game.year?:0,
            game.minPlayers?:0,
            game.maxPlayers?:0,
            game.minAge?:0,
            game.description?:"",
            game.publisher?:"",
            developers,
            game.rating?:0f,
            game.url?:"",
            game.rules?:""
        )



}