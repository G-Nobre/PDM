package com.pdm.serie1.bga.Utils.Factory

import android.os.Bundle
import androidx.lifecycle.ViewModel
import com.pdm.serie1.bga.Utils.BGAApp
import com.pdm.serie1.bga.Utils.Models.GroupDetailsViewModel

const val LIST_DETAILS = "LIST_DETAILS"

class GroupDetailsViewModelFactory(private val bundle: Bundle?) : AbstractFactory(bundle) {

    override fun getParcelableValue(): String = LIST_DETAILS

    override fun getModel(): ViewModel = GroupDetailsViewModel()

}