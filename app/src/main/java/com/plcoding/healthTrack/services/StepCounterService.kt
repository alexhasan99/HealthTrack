package com.plcoding.healthTrack.services

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class StepCounterService(
    private val context: Context,
    private val googleSignInAccount: GoogleSignInAccount?
) {
    private val _stepCount = MutableLiveData<Int>()

    fun countListener(callback: (Int?) -> Unit) {
        if (googleSignInAccount == null) {
            Log.d("StepCounterService", "Google Sign-In account is null")
            callback(null)
            return
        }
        Fitness.getHistoryClient(context, googleSignInAccount)
            .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener { result ->
                val totalSteps = result.dataPoints.firstOrNull()
                    ?.getValue(DataType.TYPE_STEP_COUNT_DELTA.fields[0])?.asInt() ?: 0
                Log.d("StepCounterService", "Total steps for today: $totalSteps")
                _stepCount.postValue(totalSteps)
                callback(totalSteps)
                //Log.d("Service", "Service Counter: $steps")
            }
            .addOnFailureListener { e ->
                Log.e("StepCounterService", "There was a problem getting steps.", e)
                callback(null)
            }
    }

    fun caloriesListener(callback: (Int?) -> Unit){
        if (googleSignInAccount == null) {
            Log.d("StepCounterService", "Google Sign-In account is null")
            callback(null)
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val endTime =
                LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val startTime = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0)
                .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            val readRequest = DataReadRequest.Builder()
                .aggregate(DataType.AGGREGATE_CALORIES_EXPENDED)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build()

            Fitness.getHistoryClient(context, googleSignInAccount)
                .readData(readRequest)
                .addOnSuccessListener { response ->
                    val totalCalories = response.buckets.flatMap { it.dataSets }
                        .flatMap { it.dataPoints }
                        .firstOrNull()
                        ?.getValue(DataType.TYPE_CALORIES_EXPENDED.fields[0])?.asFloat() ?: 0f
                    Log.d("StepCounterService", "Total calories for today: $totalCalories")
                    callback(totalCalories.toInt())
                }
                .addOnFailureListener { e ->
                    Log.e("StepCounterService", "There was a problem getting calories.", e)
                    callback(null)
                }
        }
    }
}

