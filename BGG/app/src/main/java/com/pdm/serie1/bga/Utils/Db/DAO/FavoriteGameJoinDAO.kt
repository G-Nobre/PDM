package com.pdm.serie1.bga.Utils.Db.DAO

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pdm.serie1.bga.Utils.Db.Entities.FavoriteGameJoin
import com.pdm.serie1.bga.Utils.Db.Entities.Game

@Dao
interface FavoriteGameJoinDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(vararg favorite: FavoriteGameJoin)

    @Delete
    fun delete(vararg favorite: FavoriteGameJoin)

    @Query("Select * from Games inner join FavoriteGameJoin on name = gameName where groupName = :groupName")
    fun getGamesFromFavoriteGroup(groupName:String): LiveData<List<Game>>

    @Query("Select Count(gameName) from FavoriteGameJoin where groupName = :name")
    fun getGamesCountForGroup(name:String):LiveData<Int>

}