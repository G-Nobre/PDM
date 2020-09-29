package com.pdm.serie1.bga.Utils


import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.room.Room
import androidx.work.*
import com.pdm.serie1.bga.Utils.Adapters.NAME
import com.pdm.serie1.bga.Utils.BGAApp.Companion.notificationTimer
import com.pdm.serie1.bga.Utils.Db.GameDb
import java.util.concurrent.TimeUnit


const val CHANNEL_ID = "GENIUZ_CHANNEL_TOPCHART"
const val URL = "URL"
const val COUNT = "COUNT"

class BGAApp : Application() {



    companion object {
        var notificationTimer = 15L
        var carouselSlideTimer = 5000
        lateinit var BGAWeb: BGAWebApi
        lateinit var db: GameDb
    }

    override fun onCreate() {
        super.onCreate()
        BGAWeb = BGAWebApi(applicationContext)
        db = Room
            .databaseBuilder(applicationContext, GameDb::class.java, "game-db")
            .build()
        createNotificationChannel()
    }

}

fun scheduleBackgroundWork(name: String,url:String, count: Int) {
    val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
    val inputData:Data = workDataOf(NAME to name,
        URL to url,
        COUNT to count)

    val request = PeriodicWorkRequestBuilder<WorkerNewFavorites>(notificationTimer,TimeUnit.MINUTES)
        .setInitialDelay(10, TimeUnit.SECONDS)
        .setConstraints(constraints)
        .setInputData(inputData)
        .build()

    WorkManager.getInstance(BGAApp.BGAWeb.ctx).enqueue(request)
}

fun createNotificationChannel() {
    // 1. Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    val name = "Board Games Atlas"
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val descriptionText = "A wild game appears! It's lvl00! It's over 9000!"
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        // 2. Register the channel with the system
        val notificationManager: NotificationManager =
            BGAApp.BGAWeb.ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}