package com.plcoding.healthTrack.presentation.mainScreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.plcoding.healthTrack.data.DailyActivity
import com.plcoding.healthTrack.data.UserRepository
import com.plcoding.healthTrack.data.UserStepInfo

class StepCounterViewModel(
    private val userRepository: UserRepository
) : ViewModel()  {

    val stepCount = MutableLiveData<Int>()
    val stepsGoal = MutableLiveData<Int>()
    val currentHyd = MutableLiveData<Double>()
    val goalHyd = MutableLiveData<Double>()
    val currentCalories = MutableLiveData<Int>()
    val weekPrognosis = MutableLiveData<List<DailyActivity>> ()
    val usersSteps = MutableLiveData<List<UserStepInfo>> ()

    init {
        startStepCounting()
    }

    fun startStepCounting() {
        val userId = Firebase.auth.currentUser?.uid ?: return

        userRepository.getUsersCurrentDailyStep(userId) { steps ->
            steps?.let {
                stepCount.postValue(it)
            }
        }
        userRepository.getUsersGoalDailyStep(userId) { steps ->
            steps?.let {
                stepsGoal.postValue(it)
            }
        }
        userRepository.getUsersGoalDailyHyd(userId) { curr ->
            curr?.let {
                goalHyd.postValue(it)
            }
        }

        userRepository.getUsersCurrentDailyHyd(userId) { hyd ->
            hyd?.let {
                currentHyd.postValue(it)
            }
        }
        userRepository.getUserCurrentCalories(userId) { calo ->
            calo?.let {
                currentCalories.postValue(it)
            }
        }

        userRepository.getLastSevenDaysActivities(userId) {week ->
            week.let {
                weekPrognosis.postValue(it)
            }
        }
    }

    fun saveGoalSettings(stepGoal: Int, waterGoal: Double){
        val userId = Firebase.auth.currentUser?.uid ?: return
        userRepository.saveUserGoalSettings(userId, stepGoal, waterGoal)
    }
    fun updateUserWaterIntake(waterIntake: Double){
        val userId = Firebase.auth.currentUser?.uid ?: return
        userRepository.updateUserWaterIntake(userId, waterIntake)
    }
    fun updateUserWaterIntakeGoal(waterIntakeGoal: Double){
        val userId = Firebase.auth.currentUser?.uid ?: return
        userRepository.updateUserWaterIntakeGoal(userId, waterIntakeGoal)
    }

    fun updateUserStepsGoal(stepsGoal: Int){
        val userId = Firebase.auth.currentUser?.uid ?: return
        userRepository.updateUserStepGoal(userId, stepsGoal)
    }

    fun getUsersSteps(){
        userRepository.getAllUsersWithCurrentSteps {all ->
            all.let {
                usersSteps.postValue(it)
            }
        }
    }
}
