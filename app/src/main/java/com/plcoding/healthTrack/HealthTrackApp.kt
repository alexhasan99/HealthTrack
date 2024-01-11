package com.plcoding.healthTrack

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class HealthTrackApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "running-channel",
                "Step Track Notification",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val halfGoalChannel = NotificationChannel(
                "halfGoal-channel",
                "Step Track Notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            notificationManager.createNotificationChannel(halfGoalChannel)
        }
    }

}