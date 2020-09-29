package com.pdm.serie1.bga.Utils.Factory

import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.pdm.serie1.bga.Utils.BGAApp
import com.pdm.serie1.bga.Utils.Models.FavoritesGroupViewModel

const val CHARACTERISTIC_GROUP_VIEW_MODEL = "CHARACTERISTICGROUPVIEWMODEL"

class FavoriteGroupViewModelFactory (private val bundle: Bundle?): AbstractFactory(bundle){

    override fun getModel(): ViewModel = FavoritesGroupViewModel(BGAApp.BGAWeb)

    override fun getParcelableValue(): String = CHARACTERISTIC_GROUP_VIEW_MODEL

}