package com.pdm.serie1.bga.Utils.Db.DAO

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pdm.serie1.bga.Utils.Db.Entities.DeveloperGameJoin

@Dao
interface DeveloperGameJoinDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(vararg developer: DeveloperGameJoin)

    @Delete
    fun delete(vararg developer: DeveloperGameJoin)

    @Query("Select developer from DeveloperGameJoin where gameName = :gameName")
    fun getDevelopersFromGame(gameName:String):LiveData<List<String>>
}