package com.pdm.serie1.bga.Utils.Db.DAO

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pdm.serie1.bga.Utils.Db.Entities.Group

@Dao
interface GroupsDAO {
    @Query("select * from Groups")
    fun getAllGroups(): LiveData<List<Group>>

    @Query("Select * from Groups where GName LIKE :name")
    fun getGroupsbyName(name: String): LiveData<List<Group>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertGameGroup(vararg group: Group)

    @Query("Delete from Groups")
    fun nukeGameGroups()

    @Delete
    fun deleteGameGroup(vararg group: Group)

}