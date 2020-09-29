package com.pdm.serie1.bga.Utils.Models

import android.os.AsyncTask
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pdm.serie1.bga.Activities.TAG
import com.pdm.serie1.bga.Utils.BGAApp
import com.pdm.serie1.bga.Utils.BGAWebApi
import com.pdm.serie1.bga.Utils.Db.Entities.Group
import com.pdm.serie1.bga.Utils.asyncTask

class GroupViewModel(private val bgaApi: BGAWebApi) : AbstractModel(), Parcelable {

    fun searchAllGameLists() = BGAApp.db.GroupsDAO().getAllGroups()

    fun insertGameList(name: String): LiveData<List<Group>> {
        asyncTask(
            { BGAApp.db.GroupsDAO().insertGameGroup(Group(name))},
            {Log.v(TAG, "** Inserting... **")}
        )
        return searchAllGameLists()
    }

    fun searchByName(query: String): LiveData<List<Group>> =
        BGAApp.db.GroupsDAO().getGroupsbyName("$query%")



    fun removeGameList(name: String): LiveData<List<Group>> {
        asyncTask({ BGAApp.db.GroupsDAO().deleteGameGroup(Group(name)) },
        {Log.v(TAG, "** Deleting... **")})
        return searchAllGameLists()
    }

    fun removeAllGameLists() = asyncTask(
        {BGAApp.db.GroupsDAO().nukeGameGroups()},
        { Toast.makeText(bgaApi.ctx, "All Lists Deleted!", Toast.LENGTH_SHORT).show() }
    )

    constructor(parcel: Parcel) : this(BGAApp.BGAWeb) {}

    override fun writeToParcel(parcel: Parcel, flags: Int) { }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<GroupViewModel> {
        override fun createFromParcel(parcel: Parcel): GroupViewModel {
            return GroupViewModel(parcel)
        }

        override fun newArray(size: Int): Array<GroupViewModel?> {
            return arrayOfNulls(size)
        }
    }

}