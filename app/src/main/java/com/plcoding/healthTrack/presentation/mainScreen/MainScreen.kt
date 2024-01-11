package com.plcoding.healthTrack.presentation.mainScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.plcoding.healthTrack.presentation.mainScreen.circularBar.MainCircularProgressBar
import com.plcoding.healthTrack.presentation.mainScreen.hydrationComponents.HydrationCard
import com.plcoding.healthTrack.presentation.mainScreen.hydrationComponents.WaterIntakeButton
import com.plcoding.healthTrack.presentation.mainScreen.weekBarGraph.WeekBarGraph
import com.plcoding.healthTrack.ui.theme.DeepBlue
import kotlin.math.absoluteValue

@Composable
fun MainScreen(
    navController: NavController,
    stepCounterViewModel: StepCounterViewModel = viewModel(),
    startService: () -> Unit,
    stopService: () -> Unit
) {
    val stepCount = stepCounterViewModel.stepCount.observeAsState().value
    val stepGoal = stepCounterViewModel.stepsGoal.observeAsState().value
    val currentHyd = stepCounterViewModel.currentHyd.observeAsState().value
    val hydGoal = stepCounterViewModel.goalHyd.observeAsState().value
    val currentCalories = stepCounterViewModel.currentCalories.observeAsState().value
    val weekPrognosis = stepCounterViewModel.weekPrognosis.observeAsState().value ?: listOf()

    Scaffold(
        backgroundColor = DeepBlue,
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            Row(modifier = Modifier.fillMaxWidth()) {
                WaterIntakeButton(
                    modifier = Modifier
                        .size(85.dp) // This will make it a circle with diameter 100.dp
                        .padding(16.dp),
                    onWaterIntake = { amount ->
                        stepCounterViewModel.updateUserWaterIntake(amount)
                    }
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-20).dp),
                contentAlignment = Alignment.Center
            ) {
                if (stepCount != null && stepGoal != null) {
                    MainCircularProgressBar(
                        currentSteps = stepCount.absoluteValue,
                        goalSteps = stepGoal.absoluteValue,
                        radius = 130.dp
                    )
                }
            }
           Spacer(modifier = Modifier.height(15.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-20).dp)
                    .padding(horizontal = 60.dp)
            ) {
                if (currentHyd != null && hydGoal != null) {
                    HydrationCard(
                        modifier = Modifier
                            .height(100.dp),
                        currentHyd = currentHyd.absoluteValue,
                        goalHyd = hydGoal.absoluteValue
                    )
                }
                Spacer(modifier = Modifier.width(75.dp))
                if (currentCalories != null) {
                    CaloriesCard(
                        modifier = Modifier
                            .height(100.dp),
                        currentCalories = currentCalories
                    )
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = "Step Overview",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            WeekBarGraph(weekPrognosis = weekPrognosis)
        }
    }
}
