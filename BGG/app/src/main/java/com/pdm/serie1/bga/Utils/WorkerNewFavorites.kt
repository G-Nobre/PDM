package com.pdm.serie1.bga.Utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.pdm.serie1.bga.Activities.FavoritesGroupActivity
import com.pdm.serie1.bga.Activities.TAG
import com.pdm.serie1.bga.Dtos.SearchDto
import com.pdm.serie1.bga.R
import com.pdm.serie1.bga.Utils.Adapters.NAME
import com.pdm.serie1.bga.Utils.Db.Entities.DeveloperGameJoin
import com.pdm.serie1.bga.Utils.Db.Entities.FavoriteGameJoin
import java.util.concurrent.CompletableFuture

class WorkerNewFavorites(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {
        val cf = CompletableFuture<SearchDto>()
        Log.v(TAG, "Getting Favourite Games in background...")

        val groupName = inputData.getString(NAME)
        val listURL = inputData.getString(URL)
        val listCount = inputData.getInt(COUNT, 0)

        BGAApp.BGAWeb.getFavGamesFromUrl(
            listURL!!,
            { cf.complete(it) },
            { cf.completeExceptionally(it) }
        )
        try {
            val dto = cf.get()
            Log.v(TAG, "Get favorite games for ${groupName} COMPLETED")
            val games = dto.games
            if (games.size != listCount) {
                notifyFavoriteGamesChannel()
                games.forEach {game ->
                    BGAApp.db.FavoriteGameJoinDAO().insert(FavoriteGameJoin(groupName!!,game.name!!))
                    game.artists!!.forEach {developer ->
                        BGAApp.db.DeveloperGameJoinDAO().insert(DeveloperGameJoin(developer,game.name))
                    }
                }
                return Result.success()
            }
        } catch (e: Exception) {
            Log.v(TAG, "Get favorite games for ${groupName} FAILED")
            Result.failure()
            throw e
        }
        return Result.success()
    }

    private fun notifyFavoriteGamesChannel() {
        val intent = Intent(applicationContext, FavoritesGroupActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(applicationContext, 0, intent, 0)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.app_icon)
            .setContentTitle("Favorite games")
            .setContentText("Update on favorite games from Board Game Atlas!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        /**
         * 5. Send the notification
         */
        NotificationManagerCompat
            .from(applicationContext)
            .notify(R.string.notification, notification)
    }

    private fun dtoToModel(groupName: String, dto: SearchDto): Array<FavoriteGameJoin> {
        return dto.games.map { FavoriteGameJoin(groupName, it.name!!) }.toTypedArray()
    }


}