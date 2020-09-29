package com.pdm.serie1.bga.Utils.Db.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "FavoritesGroups")
data class FavoriteGroups(
    @PrimaryKey val FGName: String, // group name
    val url: String = ""
)