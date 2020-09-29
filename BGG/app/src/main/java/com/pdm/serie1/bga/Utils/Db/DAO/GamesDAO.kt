package com.pdm.serie1.bga.Utils.Db.DAO

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pdm.serie1.bga.Utils.Db.Entities.Game

@Dao
interface GamesDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertGame(vararg games: Game)

    @Delete
    fun delete(game: Game)

    @Query("Select * from Games where name = :name")
    fun getGameDetails(name: String) : LiveData<Game>

}
