package com.plcoding.healthTrack.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.plcoding.healthTrack.R
import com.plcoding.healthTrack.data.DailyActivity
import com.plcoding.healthTrack.data.UserRepository
import com.plcoding.healthTrack.presentation.signIn.GoogleAuthUiClient
import java.time.LocalDate
import java.util.Calendar
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.TimeUnit


class BackgroundServices: Service() {
    private val handler = Handler()
    private var steps : Int = 0
    private var calories : Int = 0
    private var stepGoal: Int = 0
    private var halfGoalNotified: Boolean = false
    private val stepCountRunnable = object : Runnable {
        override fun run() {
            var tempSteps: Int? = null
            var tempCalories: Int? = null

            val updateIfReady = {
                val localSteps = tempSteps
                val localCalories = tempCalories
                if (localSteps != null && localCalories != null) {
                    if (steps != localSteps || calories != localCalories.toInt()) {
                        steps = localSteps
                        calories = localCalories.toInt()
                        updateNotification()
                        updateDatabaseDailySteps(steps, calories)
                    }
                    handler.postDelayed(this, 10 * 1000)
                }
            }

            stepCounterService?.countListener { counter ->
                if (counter != null) {
                    tempSteps = counter
                    updateIfReady()
                } else {
                    handler.postDelayed(this, 10 * 1000)
                }
            }

            stepCounterService?.caloriesListener { calo ->
                if (calo != null) {
                    tempCalories = calo
                    updateIfReady()
                }
            }
        }
    }


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private lateinit var notificationManager: NotificationManager

    private val dailyUpdateTask = object : TimerTask() {
        override fun run() {

            saveDailyActivity()
        }
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        scheduleDailyUpdateTask()
    }

    private fun scheduleDailyUpdateTask() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 1)
            set(Calendar.MINUTE, 41)
        }
        // Om tiden redan har passerat för idag, gå till nästa dag
        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        Timer().scheduleAtFixedRate(dailyUpdateTask, calendar.time, TimeUnit.DAYS.toMillis(1))
    }

    private fun saveDailyActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val currentDate = LocalDate.now()
            val dailyActivity = DailyActivity(calories ,steps, currentDate.toString(), 0.0)
            val userId = googleAuthUiClient?.getSignedInUser()?.userId
            if (userId != null) {
                userRepository?.addDailyActivity(userId, dailyActivity) {
                    // Nollställ currentDailyHydration efter att dailyActivity har sparats
                    userRepository?.resetCurrentHydration(userId)
                }
            }
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            Actions.START.toString() -> start()
            Actions.STOP.toString() -> {
                handler.removeCallbacks(stepCountRunnable)
                stopSelf()
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val userId = googleAuthUiClient?.getSignedInUser()?.userId
        userId?.let { fetchStepGoal(it) }
        handler.post(stepCountRunnable)
        startForegroundService()
    }

    override fun onDestroy() {
        handler.removeCallbacks(stepCountRunnable)
        super.onDestroy()
    }


    private fun startForegroundService() {

        val notification = buildNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Step Tracking Active")
            .setContentText("Your steps: $steps")
            .setSmallIcon(R.drawable.baseline_directions_run_24)
            .build()
    }


    private fun updateNotification() {
        val notification = buildNotification()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun updateDatabaseDailySteps(currentSteps: Int, caloriesBurned: Int) {
        val userId = googleAuthUiClient?.getSignedInUser()?.userId

        if (userId != null) {
            userRepository?.getUsersCurrentDailyStep(userId) { dataBaseCount ->
                val newStepCount = if (dataBaseCount != null && currentSteps > dataBaseCount) {
                    currentSteps
                } else {
                    dataBaseCount ?: currentSteps
                }
                userRepository?.updateUserStepCountAndCalories(userId, newStepCount, caloriesBurned)
            }
        }

        if (stepGoal > 0 && currentSteps >= stepGoal / 2 && !halfGoalNotified) {
            sendHalfGoalNotification()
            halfGoalNotified = true
        }
    }

    private fun fetchStepGoal(userId: String) {
        userRepository?.getUsersGoalDailyStep(userId) { goal ->
            stepGoal = goal ?: 0
        }
    }

    private fun sendHalfGoalNotification() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Halvvägs där!")
            .setContentText("Du har nått halva ditt stegmål för idag.")
            .setSmallIcon(R.drawable.twotone_calendar_month_24)
            .build()
        notificationManager.notify(NOTIFICATION_ID_HALF_GOAL, notification)
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val NOTIFICATION_ID_HALF_GOAL = 2
        private const val CHANNEL_ID = "running-channel"
        @SuppressLint("StaticFieldLeak")
        var stepCounterService: StepCounterService? = null
        var userRepository: UserRepository? = null
        @SuppressLint("StaticFieldLeak")
        var googleAuthUiClient: GoogleAuthUiClient? = null
    }

    enum class Actions {
        START, STOP
    }
}