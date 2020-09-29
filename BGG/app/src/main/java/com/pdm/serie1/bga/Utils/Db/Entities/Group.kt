package com.pdm.serie1.bga.Utils.Db.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Groups")
data class Group(
    @PrimaryKey val GName: String
)
