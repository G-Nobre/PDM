package com.pdm.serie1.bga.Utils.Db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pdm.serie1.bga.Utils.Db.DAO.*
import com.pdm.serie1.bga.Utils.Db.Entities.*

@Database(
    entities = [Game::class, Group::class, GroupGameJoin::class,DeveloperGameJoin::class, FavoriteGroups::class,FavoriteGameJoin::class],
    version = 1,
    exportSchema = false)
abstract class GameDb : RoomDatabase() {

    abstract fun GamesDAO(): GamesDAO
    abstract fun GroupsDAO(): GroupsDAO
    abstract fun GamesOfAListDAO(): GamesOfAListDAO
    abstract fun DeveloperGameJoinDAO(): DeveloperGameJoinDAO
    abstract fun FavoriteGroupsDAO(): FavoritesGroupsDAO
    abstract fun FavoriteGameJoinDAO():FavoriteGameJoinDAO
}