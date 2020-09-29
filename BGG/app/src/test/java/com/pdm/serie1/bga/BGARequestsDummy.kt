package com.pdm.serie1.bga

import com.google.gson.Gson
import com.pdm.serie1.bga.Dtos.SearchDto
import com.pdm.serie1.bga.Utils.BGA_SEARCH
import org.junit.Test

import org.junit.Assert.*
import java.io.InputStreamReader
import java.net.URL


/**
 * Example local unit test, which will updateRecyclerViewData on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class BGARequestsDummy {
    @Test
    fun shouldMakeRequests() {
        val path = String.format(BGA_SEARCH,"Cat")
        val reader = InputStreamReader(URL(path).openStream())

        val gson = Gson()
        var searchDto : SearchDto = gson.fromJson<SearchDto>(reader, SearchDto::class.java)
        assertEquals(searchDto.games[0].name, "Cat Lady")
    }
}
