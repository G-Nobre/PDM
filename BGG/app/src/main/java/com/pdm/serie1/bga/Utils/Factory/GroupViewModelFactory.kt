package com.pdm.serie1.bga.Utils.Factory

import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.pdm.serie1.bga.Utils.BGAApp
import com.pdm.serie1.bga.Utils.Models.GroupViewModel

const val LISTVIEWMODEL = "LISTVIEWMODEL"

class GroupViewModelFactory(private val bundle: Bundle?) : AbstractFactory(bundle){

    override fun getParcelableValue(): String = LISTVIEWMODEL

    override fun getModel(): ViewModel = GroupViewModel(BGAApp.BGAWeb)

}
