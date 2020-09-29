package com.pdm.serie1.bga.Utils.Models

import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.LiveData
import com.pdm.serie1.bga.Utils.BGAApp
import com.pdm.serie1.bga.Utils.Db.Entities.DeveloperGameJoin
import com.pdm.serie1.bga.Utils.Db.Entities.Game
import com.pdm.serie1.bga.Utils.Db.Entities.GroupGameJoin
import com.pdm.serie1.bga.Utils.asyncTask

class GroupDetailsViewModel() : AbstractModel(), Parcelable {

    fun getAllGamesInAGroup(groupName: String): LiveData<List<Game>> =
        BGAApp.db.GamesOfAListDAO().getAllGamesFromGroup(groupName)

    override fun describeContents(): Int = 0

    fun insertGameInGroup(groupName: String, game: Game): LiveData<List<Game>> {
        asyncTask( {
            BGAApp.db.GamesDAO().insertGame(game)
            BGAApp.db.GamesOfAListDAO().insertGameInAGroup(
                GroupGameJoin(groupName, game.name)
            )
        },{})
        return getAllGamesInAGroup(groupName)
    }

    fun getGamesInGroupByName(gameName: String): LiveData<List<GroupGameJoin>> =
        BGAApp.db.GamesOfAListDAO().getGameInGroupByName(gameName)

    fun deleteGameByName(game: Game) = asyncTask({ BGAApp.db.GamesDAO().delete(game) }, {})

    fun deleteGameFromGroup(groupName: String, game: Game): LiveData<List<Game>> {
        asyncTask({
            BGAApp.db.GamesOfAListDAO().deleteGameFromGroup(GroupGameJoin(groupName, game.name))
        }, {})
        return getAllGamesInAGroup(groupName)
    }

    fun getDevelopers(gameName: String) =
        BGAApp.db.DeveloperGameJoinDAO().getDevelopersFromGame(gameName)

    fun insertDevelopers(developer: String, gameName: String) {
        asyncTask({
            BGAApp.db.DeveloperGameJoinDAO().insert(DeveloperGameJoin(developer, gameName))
        }, {})
    }


    constructor(parcel: Parcel) : this()

    override fun writeToParcel(p0: Parcel?, p1: Int) {

    }

    companion object CREATOR : Parcelable.Creator<GroupDetailsViewModel> {
        override fun createFromParcel(parcel: Parcel): GroupDetailsViewModel {
            return GroupDetailsViewModel(parcel)
        }

        override fun newArray(size: Int): Array<GroupDetailsViewModel?> {
            return arrayOfNulls(size)
        }
    }

}