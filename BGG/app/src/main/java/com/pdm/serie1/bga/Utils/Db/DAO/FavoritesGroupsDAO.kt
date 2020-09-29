package com.pdm.serie1.bga.Utils.Db.DAO

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pdm.serie1.bga.Utils.Db.Entities.FavoriteGroups

@Dao
interface FavoritesGroupsDAO {
    @Query("select * from FavoritesGroups")
    fun getAllFavoriteGroups(): LiveData<List<FavoriteGroups>>

    @Query("Select * from FavoritesGroups where FGName LIKE :name")
    fun getFavoriteGroupsbyName(name: String): LiveData<List<FavoriteGroups>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFavoriteGroup(vararg favoriteGroups: FavoriteGroups)

    @Query("Delete from FavoritesGroups")
    fun nukeFavoriteGroups()

    @Delete
    fun deleteFavoriteGroup(vararg favoriteGroups: FavoriteGroups)
}