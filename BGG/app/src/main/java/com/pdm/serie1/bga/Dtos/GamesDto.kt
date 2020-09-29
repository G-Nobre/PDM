package com.pdm.serie1.bga.Dtos

class GamesDto(
    val images: ImageDto = ImageDto("","",""),
    val name: String? = "",
    val year_published: Int? = 0,
    val min_players: Int? = 0,
    val max_players: Int? = 0,
    val min_age: Int? = 0,
    val description_preview: String? = "",
    val primary_publisher: String? = "",
    val artists: Array<String>? = emptyArray(),
    val average_user_rating: Float? = 0f,
    val official_url: String? = "",
    val rules_url: String? = ""
)