package com.pdm.serie1.bga.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.WorkManager
import com.pdm.serie1.bga.R
import com.pdm.serie1.bga.Utils.BGAApp
import com.pdm.serie1.bga.Utils.insertTextMessage
import com.pdm.serie1.bga.Utils.scheduleBackgroundWork

class SettingsActivity : AppCompatActivity() {

    private val carouselSpinner by lazy {
        findViewById<Spinner>(R.id.carouselSpinner)
    }

    private val workerTimerSpinner by lazy {
        findViewById<Spinner>(R.id.workerTimerSpinner)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val settingsPref: SharedPreferences = getSharedPreferences("", Context.MODE_PRIVATE)
        val edit = settingsPref.edit()

        BGAApp.notificationTimer = settingsPref.getLong("workerTimer", BGAApp.notificationTimer)
        BGAApp.carouselSlideTimer = settingsPref.getInt("carouselTimer", BGAApp.carouselSlideTimer)

        createSpinner(carouselSpinner, R.array.carousel_options)
        val carouselPos = getCarouselPosition()
        carouselSpinner.setSelection(carouselPos)
        createSpinner(workerTimerSpinner, R.array.timer_options)
        workerTimerSpinner.setSelection(getNotificationPosition())


        val context = this

        title = getString(R.string.settingsTitle)

        workerTimerSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val timer = workerTimerSpinner.selectedItem.toString().toLong()

                if (timer != BGAApp.notificationTimer) {
                    BGAApp.notificationTimer = timer
                    WorkManager.getInstance(BGAApp.BGAWeb.ctx).cancelAllWork()

                    BGAApp.db.FavoriteGroupsDAO().getAllFavoriteGroups().observe(context, Observer {
                        it.forEach { group ->
                            BGAApp.db.FavoriteGameJoinDAO().getGamesCountForGroup(group.FGName)
                                .observe(context, Observer { count ->
                                    scheduleBackgroundWork(group.FGName, group.url, count)
                                })
                        }
                    })
                    insertTextMessage(context, getString(R.string.notification_time_changed))
                    edit.putLong("workerTimer", timer)
                    edit.apply()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        carouselSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val value = carouselSpinner.selectedItem.toString().toInt() * 1000
                if (BGAApp.carouselSlideTimer != value) {
                    BGAApp.carouselSlideTimer = value
                    insertTextMessage(context, getString(R.string.carousel_time_changed))
                    edit.putInt("carouselTimer", value)
                    edit.apply()
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

    }

    private fun getCarouselPosition(): Int =
        when (BGAApp.carouselSlideTimer) {
            3000 -> 0
            5000 -> 1
            10000 -> 2
            15000 -> 3
            else -> 0
        }

    private fun getNotificationPosition(): Int =
        when (BGAApp.notificationTimer) {
            15L -> 0
            30L -> 1
            60L -> 2
            120L -> 3
            else -> 0
        }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun createSpinner(spinner: Spinner, arrayOptions: Int) {
        ArrayAdapter.createFromResource(
            this,
            arrayOptions,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = it
        }
    }
}
