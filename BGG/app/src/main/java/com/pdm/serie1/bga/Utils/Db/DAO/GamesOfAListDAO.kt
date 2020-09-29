package com.pdm.serie1.bga.Utils.Db.DAO

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pdm.serie1.bga.Utils.Db.Entities.Game
import com.pdm.serie1.bga.Utils.Db.Entities.GroupGameJoin

@Dao
interface GamesOfAListDAO {

    @Query(""" 
        Select * from Games INNER JOIN GroupGameJoin ON 
        Games.name = GroupGameJoin.gameName
        where GroupGameJoin.groupName = :groupName
        """)
    fun getAllGamesFromGroup(groupName: String): LiveData<List<Game>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertGameInAGroup(vararg groupGame: GroupGameJoin)

    @Delete
    fun deleteGameFromGroup(vararg groupGame: GroupGameJoin)

    @Query("Select * from GroupGameJoin where gameName = :gameName")
    fun getGameInGroupByName(gameName:String):LiveData<List<GroupGameJoin>>
}