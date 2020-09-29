package com.pdm.serie1.bga.Utils

import android.os.AsyncTask
import android.util.Log
import com.pdm.serie1.bga.Activities.TAG

fun asyncTask(method: () -> Unit, onPostExecute: () -> Unit) {
    object : AsyncTask<Unit, Int, Unit>() {
        override fun doInBackground(vararg p0: Unit?) {
            method()
        }
        override fun onPostExecute(result: Unit?) {
            onPostExecute()
        }
    }.execute()

}

