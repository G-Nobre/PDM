package com.pdm.serie1.bga.Utils

import com.pdm.serie1.bga.Dtos.CategoriesMechanicsDto

enum class FavOption {
    MECHANIC {
        override fun getUrl(
            page: Int,
            publisher: String?,
            artist: String?,
            mechanics: Array<String>,
            categories: Array<String>
        ): String = BGA_ALL_MECHANICS
    },
    CATEGORY {
        override fun getUrl(
            page: Int,
            publisher: String?,
            artist: String?,
            mechanics: Array<String>,
            categories: Array<String>
        ): String = BGA_ALL_CATEGORIES
    },
    FAVORITES {
        override fun getUrl(
            page: Int,
            publisher: String?,
            artist: String?,
            mechanics: Array<String>,
            categories: Array<String>
        ): String {
            val publishers: String = if (!publisher.isNullOrEmpty())"publisher=${publisher}&" else ""
            val artists = if (!artist.isNullOrEmpty()) "artist=${artist}&" else ""
            val mechanic =
                if (mechanics.isNotEmpty()) "mechanics=${getString(mechanics)}&" else ""
            val categorie =
                if (categories.isNotEmpty()) "categories=${getString(categories)}&" else ""
            return String.format(
                BGA_HOST,
                30 * (page - 1)
            ) + publishers + artists + mechanic + categorie + "client_id=$CLIENT_ID"
        }
    };

    abstract fun getUrl(
        page: Int = 0,
        publisher: String? = null,
        artist: String? = null,
        mechanics: Array<String> = emptyArray(),
        categories: Array<String> = emptyArray()
    ): String

    fun getString(Array: Array<String>): String {
        var string = ""
        Array.forEach {
            string += "${it},"
        }
        return string.substring(0, string.length - 1)
    }
}