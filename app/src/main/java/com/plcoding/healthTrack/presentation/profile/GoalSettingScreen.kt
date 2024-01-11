package com.plcoding.healthTrack.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.plcoding.healthTrack.ui.theme.DarkBlue
import com.plcoding.healthTrack.ui.theme.DeepBlue

@Composable
fun GoalSettingScreen(
    onGoalsSet: (Int, Double) -> Unit
) {
    var stepGoal by remember { mutableStateOf("") }
    var waterGoal by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Scaffold(
        backgroundColor = DeepBlue,
        content = {
            Box(modifier = Modifier.padding(it), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (showError) {
                        Text("Vänligen ange ett mål större än 0", color = Color.Red)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    TextField(
                        value = stepGoal,
                        onValueChange = { stepGoal = it },
                        label = { Text("Steps Goal", color = Color.White) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = waterGoal,
                        onValueChange = { waterGoal = it },
                        label = { Text("Water Intake Goal", color = Color.White) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            val stepGoalValue = stepGoal.toIntOrNull() ?: 0
                            val waterGoalValue = waterGoal.toDoubleOrNull() ?: 0.0
                            if (stepGoalValue > 0 && waterGoalValue > 0.0) {
                                showError = false
                                onGoalsSet(stepGoalValue, waterGoalValue)
                            } else {
                                showError = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = DarkBlue)
                    ) {
                        Text("Save Goals", color = Color.White)
                    }
                }
            }
        }
    )
}

