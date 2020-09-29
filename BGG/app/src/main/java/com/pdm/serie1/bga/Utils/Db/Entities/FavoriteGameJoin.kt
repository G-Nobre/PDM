package com.pdm.serie1.bga.Utils.Db.Entities

import androidx.room.Entity
import androidx.room.ForeignKey


@Entity(
    primaryKeys = ["groupName", "gameName"],
    foreignKeys = [
        ForeignKey(
            onDelete = ForeignKey.CASCADE,
            entity = FavoriteGroups::class,
            parentColumns = ["FGName"],
            childColumns = ["groupName"]
        ),
        ForeignKey(
            onDelete = ForeignKey.CASCADE,
            entity = Game::class,
            parentColumns = ["name"],
            childColumns = ["gameName"]
        )
    ]
)

data class FavoriteGameJoin(
    val groupName: String,
    val gameName: String
)