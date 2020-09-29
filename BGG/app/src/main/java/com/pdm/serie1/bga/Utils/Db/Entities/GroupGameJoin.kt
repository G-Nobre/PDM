package com.pdm.serie1.bga.Utils.Db.Entities

import androidx.room.Entity
import androidx.room.ForeignKey


@Entity(
    primaryKeys = ["groupName", "gameName"],
    foreignKeys = [
        ForeignKey(
            onDelete = ForeignKey.CASCADE,
            entity = Group::class,
            parentColumns = ["GName"],
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
data class GroupGameJoin(
    val groupName: String,
    val gameName: String
)