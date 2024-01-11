package com.plcoding.healthTrack.data

data class UserProfile(
    val userId: String,
    val email: String?,
    val username: String,
    val dailyStepGoal: Int = 0,
    val dailyHydrationGoal: Double = 0.0,
    val currentDailyStep: Int = 0,
    val currentDailyHydration: Double = 0.0,
    val currentDailyCalories: Int = 0,
    val weekTotalSteps: Int = 0,
    val weekPrognosis: Map<String, DailyActivity> = emptyMap()
)
data class DailyActivity(
    val calories: Int = 0,
    val dailyStep: Int = 0,
    val date: String= "",
    var hydration: Double = 0.0,
)

data class UserStepInfo(
    val username: String,
    val currentSteps: Int,
    val userId: String
)
