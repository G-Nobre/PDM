package com.pdm.serie1.bga.Utils.Factory

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pdm.serie1.bga.Activities.TAG
import com.pdm.serie1.bga.Utils.BGAApp
import com.pdm.serie1.bga.Utils.Models.GamesViewModel
import kotlin.reflect.typeOf

const val GAMESVIEWMODEL = "GAMESVIEWMODEL"

class GamesViewModelFactory(private val bundle: Bundle?) : AbstractFactory(bundle) {

    override fun getModel(): ViewModel = GamesViewModel(BGAApp.BGAWeb)

    override fun getParcelableValue(): String = GAMESVIEWMODEL

}