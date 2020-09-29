package com.pdm.serie1.bga.Utils.Db.Entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE

@Entity(
    primaryKeys = ["developer", "gameName"],
    foreignKeys = [
        ForeignKey(
            onDelete = CASCADE,
            entity = Game::class,
            parentColumns = ["name"],
            childColumns = ["gameName"]
        )
    ]
)
data class DeveloperGameJoin(
    val developer: String,
    val gameName: String
)