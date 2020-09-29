package com.pdm.serie1.bga.Utils.Db.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pdm.serie1.bga.Dtos.GamesDto

@Entity(tableName = "Games")
data class Game(
    @PrimaryKey
    val name: String = "",
    val year: Int? = 0,
    val minPlayers: Int? = 0,
    val maxPlayers: Int? = 0,
    val minAge: Int? = 0,
    val publisher: String? = "",
    val rating: Float? = 0f,
    val rules: String? = "",
    val description: String? = "",
    val imageSmallUri: String? = "",
    val imageOriginalUri: String? = "",
    val imageMediumUri: String? = "",
    val url: String? = ""
)

fun dtoToDao(game: GamesDto): Game = Game(
    game.name ?: "",
    game.year_published ?: 0,
    game.min_players ?: 0,
    game.max_players ?: 0,
    game.min_age ?: 0,
    game.primary_publisher ?: "",
    game.average_user_rating ?: 0f,
    game.rules_url ?: "",
    game.description_preview ?: "",
    game.images.small ?: "",
    game.images.original ?: "",
    game.rules_url ?: ""
)