package com.pdm.serie1.bga.Utils

import android.content.Context
import android.os.AsyncTask
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.pdm.serie1.bga.Dtos.*

const val CLIENT_ID = "E1Z5Ye5WzX"
const val BGA_HOME = "https://www.boardgameatlas.com/api/"

const val BGA_HOST = "${BGA_HOME}search?skip=%d&"

const val BGA_SEARCH = "${BGA_HOST}name=%s&limit=31&client_id=$CLIENT_ID"
const val BGA_POPULAR_GAMES = "${BGA_HOST}limit=31&orderby=popularity&client_id=$CLIENT_ID"
const val BGA_COMPANY_GAMES = "${BGA_HOST}publisher=%s&limit=31&client_id=$CLIENT_ID"
const val BGA_DEVELOPER_GAMES = "${BGA_HOST}artist=%s&limit=31&client_id=$CLIENT_ID"
const val BGA_ALL_MECHANICS = "${BGA_HOME}game/mechanics?&client_id=$CLIENT_ID"
const val BGA_ALL_CATEGORIES = "${BGA_HOME}game/categories?&client_id=$CLIENT_ID"
const val INDEX_OF_COMPANY = 1
const val INDEX_OF_DEVELOPER = 2



class BGAWebApi(val ctx: Context) {

    private val queue = Volley.newRequestQueue(ctx)
    private val gson = Gson()
    fun searchForInfo(
        searchInfo: String,
        page: Int,
        option: Option,
        onSuccess: (SearchDto) -> Unit,
        onError: (VolleyError) -> Unit
    ) {
        val stringRequest = StringRequest(
            Request.Method.GET,
            option.getUrl(searchInfo, page),
            Response.Listener<String> { response -> executeAsyncTask(response, onSuccess) },
            Response.ErrorListener(onError)
        )
        queue.add(stringRequest)
    }

    fun searchForFavGames(
        page: Int,
        publisher: String,
        artist:String,
        mechanics:Array<String>,
        categories:Array<String>,
        option: FavOption,
        onSuccess: (SearchDto) -> Unit,
        onError: (VolleyError) -> Unit
    ):String{
        val url = option.getUrl(page,publisher,artist,mechanics,categories)
        println(url)
        val stringRequest = StringRequest(Request.Method.GET,
            url,
            Response.Listener<String> { response -> executeAsyncTask(response, onSuccess) },
            Response.ErrorListener(onError)
        )
        queue.add(stringRequest)
        return url
    }

    fun getFavGamesFromUrl(
        url:String,
        onSuccess: (SearchDto) -> Unit,
        onError: (VolleyError) -> Unit){

        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            Response.Listener{ executeAsyncTask(it,onSuccess)},
            Response.ErrorListener(onError)
        )
        print("Adding StringRequest!")
        queue.add(stringRequest)
    }

    fun getAllMechanics(
        option: FavOption,
        onSuccess: (Mechanics) -> Unit,
        onError: (VolleyError) -> Unit){
        val stringRequest = StringRequest(
            Request.Method.GET,
            option.getUrl(),
            Response.Listener { executeMechanicsAsyncTask(it,onSuccess)},
            Response.ErrorListener(onError)
        )
        queue.add(stringRequest)
    }

    fun getAllCategories(
        option: FavOption,
        onSuccess: (Categories) -> Unit,
        onError: (VolleyError) -> Unit){
            val stringRequest = StringRequest(
                Request.Method.GET,
                option.getUrl(),
                Response.Listener { executeCategoriesAsyncTask(it,onSuccess)},
                Response.ErrorListener(onError)
            )
            queue.add(stringRequest)
    }

    private fun executeAsyncTask(response: String?, onSuccess: (SearchDto) -> Unit) =
        object : AsyncTask<String, Int, SearchDto>() {
            override fun doInBackground(vararg params: String?): SearchDto =
                gson.fromJson<SearchDto>(response, SearchDto::class.java)

            override fun onPostExecute(result:SearchDto) = onSuccess(result!!)
        }.execute(response)


    private fun executeCategoriesAsyncTask(response: String?,onSuccess: (Categories) -> Unit)=
        object : AsyncTask<String, Int, Categories>() {
            override fun doInBackground(vararg params: String?): Categories =
                gson.fromJson<Categories>(response, Categories::class.java)

            override fun onPostExecute(result:Categories) = onSuccess(result!!)
        }.execute(response)

    private fun executeMechanicsAsyncTask(response: String?,onSuccess: (Mechanics) -> Unit)=
        object : AsyncTask<String, Int, Mechanics>() {
            override fun doInBackground(vararg params: String?): Mechanics =
                gson.fromJson<Mechanics>(response, Mechanics::class.java)

            override fun onPostExecute(result:Mechanics) = onSuccess(result!!)
        }.execute(response)


}
