package com.pdm.serie1.bga.Utils
var SKIP = 30
enum class Option {
    GAME {
        override fun getUrl(name: String, page: Int): String {
            return String.format(BGA_SEARCH, SKIP * (page - 1), name)
        }
    },
    COMPANY {
        override fun getUrl(name: String, page: Int): String =
            String.format(BGA_COMPANY_GAMES, SKIP * (page - 1), name)
    },
    DEVELOPER {
        override fun getUrl(name: String, page: Int): String =
            String.format(BGA_DEVELOPER_GAMES, SKIP * (page - 1), name)
    },
    MOST_POPULAR_GAMES {
        override fun getUrl(name: String, page: Int): String =
            String.format(BGA_POPULAR_GAMES, SKIP * (page - 1))
    };

    abstract fun getUrl(name: String, page: Int): String

}