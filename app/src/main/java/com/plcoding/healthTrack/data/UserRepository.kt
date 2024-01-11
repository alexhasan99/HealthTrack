package com.plcoding.healthTrack.data


import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class UserRepository(private val firebaseDatabase: FirebaseDatabase) {

    fun saveUserProfile(userProfile: UserProfile) {
        Log.d("Email", "${userProfile.email}")
        val databaseReference = firebaseDatabase.getReference("users")
        databaseReference.child(userProfile.userId).setValue(userProfile)

    }

    fun saveUserGoalSettings(userId: String, stepGoal: Int, waterGoal: Double) {
        val databaseReference = firebaseDatabase.getReference("users/$userId")
        databaseReference.child("dailyStepGoal").setValue(stepGoal)
        databaseReference.child("dailyHydrationGoal").setValue(waterGoal)
    }

    fun getUsersCurrentDailyStep(userId: String, callback: (Int?) -> Unit) {
        val databaseReference = firebaseDatabase.getReference("users/$userId/currentDailyStep")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Försök att hämta värdet för currentDailyStep
                val stepCount = dataSnapshot.getValue(Int::class.java)
                callback(stepCount)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("GetUserStepCount", "Error: ${databaseError.message}")
                callback(null) // Skicka null vid fel
            }
        })
    }

    fun getUsersGoalDailyStep(userId: String, callback: (Int?) -> Unit) {
        val databaseReference = firebaseDatabase.getReference("users/$userId/dailyStepGoal")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Försök att hämta värdet för currentDailyStep
                val stepCount = dataSnapshot.getValue(Int::class.java)
                callback(stepCount)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("GetUserStepCount", "Error: ${databaseError.message}")
                callback(null) // Skicka null vid fel
            }
        })
    }

    //fun getUser


    fun getUsersCurrentDailyHyd(userId: String, callback: (Double?) -> Unit) {
        val databaseReference = firebaseDatabase.getReference("users/$userId/currentDailyHydration")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Försök att hämta värdet för currentDailyStep
                val stepCount = dataSnapshot.getValue(Double::class.java)
                callback(stepCount)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("GetUserStepCount", "Error: ${databaseError.message}")
                callback(null) // Skicka null vid fel
            }
        })
    }

    fun getUsersGoalDailyHyd(userId: String, callback: (Double?) -> Unit) {
        val databaseReference = firebaseDatabase.getReference("users/$userId/dailyHydrationGoal")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Försök att hämta värdet för currentDailyStep
                val stepCount = dataSnapshot.getValue(Double::class.java)
                callback(stepCount)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("GetUserStepCount", "Error: ${databaseError.message}")
                callback(null) // Skicka null vid fel
            }
        })
    }

    fun updateUserStepCountAndCalories(userId: String, steps: Int, calories: Int) {
        val databaseReference = firebaseDatabase.getReference("users/$userId")
        databaseReference.child("currentDailyStep").setValue(steps)
        databaseReference.child("currentDailyCalories").setValue(calories)
    }


    fun updateUserWaterIntakeGoal(userId: String, goal: Double) {
        val databaseReference = firebaseDatabase.getReference("users/$userId")
        databaseReference.child("dailyHydrationGoal").setValue(goal)
    }

    fun updateUserStepGoal(userId: String, stepGoal: Int) {
        val databaseReference = firebaseDatabase.getReference("users/$userId")
        databaseReference.child("dailyStepGoal").setValue(stepGoal)
    }

    fun updateUserWaterIntake(userId: String, waterlevel: Double) {
        val databaseReference = firebaseDatabase.getReference("users/$userId")
        databaseReference.child("currentDailyHydration").setValue(ServerValue.increment(waterlevel))
    }

    fun getUserCurrentCalories(userId: String, callback: (Int?) -> Unit) {
        val databaseReference = firebaseDatabase.getReference("users/$userId/currentDailyCalories")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Försök att hämta värdet för currentDailyStep
                val calories = dataSnapshot.getValue(Int::class.java)
                callback(calories)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("GetUserStepCount", "Error: ${databaseError.message}")
                callback(null) // Skicka null vid fel
            }
        })
    }

    private fun getUsersCurrentDailyHyda(userId: String, callback: (Double?) -> Unit) {
        val databaseReference = firebaseDatabase.getReference("users/$userId/currentDailyHydration")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val hydration = dataSnapshot.getValue(Double::class.java)
                Log.d("Firebase", "Current hydration fetched: $hydration")
                callback(hydration)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase", "Error fetching hydration: ${databaseError.message}")
                callback(null)
            }
        })
    }

    fun addDailyActivity(userId: String, dailyActivity: DailyActivity, onSuccess: () -> Unit) {
        val databaseReference = firebaseDatabase.getReference("users/$userId/weekPrognosis")
        getUsersCurrentDailyHyda(userId) { hydration ->
            if (hydration != null) {
                // Sätt hydration för dailyActivity
                dailyActivity.hydration = hydration
                // Spara dailyActivity i weekPrognosis
                databaseReference.child(dailyActivity.date.toString())
                    .setValue(dailyActivity).addOnSuccessListener {
                        onSuccess() // Kalla på onSuccess när dailyActivity har sparats
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firebase", "Error saving daily activity: $e")
                    }
            }
        }
    }

    fun resetCurrentHydration(userId: String){
        val databaseReference = firebaseDatabase.getReference("users/$userId")
        databaseReference.child("currentDailyHydration").setValue(0.0)
    }

    fun userProfileExists(userId: String, callback: (Boolean) -> Unit) {
        val databaseReference = firebaseDatabase.getReference("users/$userId")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val exists = dataSnapshot.exists()
                callback(exists)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(
                    "UserProfileExists",
                    "Error checking if user exists: ${databaseError.message}"
                )
                callback(false) // Anta att profilen inte existerar vid fel
            }
        })
    }

    fun getLastSevenDaysActivities(userId: String, callback: (List<DailyActivity>) -> Unit) {
        val lastSevenDays = getLastSevenDaysDates()
        val activities = mutableListOf<DailyActivity>()

        lastSevenDays.forEach { date ->
            val dailyActivityRef = firebaseDatabase.getReference("users/$userId/weekPrognosis/$date")
            dailyActivityRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.getValue(DailyActivity::class.java)?.let { activity ->
                        activities.add(activity)
                        if (activities.size == lastSevenDays.size) {
                            callback(activities)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error getting daily activity: ${error.message}")
                }
            })
        }
    }

    private fun getLastSevenDaysDates(): List<String> {
        val dates = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        // Start from yesterday
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        for (i in 0 until 7) {
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            dates.add(date)
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        return dates
    }

    fun getAllUsersWithCurrentSteps(callback: (List<UserStepInfo>) -> Unit) {
        val usersRef = firebaseDatabase.getReference("users")
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userStepInfoList = mutableListOf<UserStepInfo>()
                for (userSnapshot in snapshot.children) {
                    val userId = userSnapshot.child("userId").getValue(String::class.java)
                    val username = userSnapshot.child("username").getValue(String::class.java)
                    val currentSteps = userSnapshot.child("currentDailyStep").getValue(Int::class.java) ?: 0
                    if (username != null && userId != null) {
                        userStepInfoList.add(UserStepInfo(username, currentSteps, userId))
                    }
                }
                callback(userStepInfoList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Firebase", "Error fetching user step info: ${databaseError.message}")
            }
        })
    }
}
