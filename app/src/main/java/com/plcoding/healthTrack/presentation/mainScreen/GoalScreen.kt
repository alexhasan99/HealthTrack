package com.plcoding.healthTrack.presentation.mainScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.plcoding.healthTrack.R
import com.plcoding.healthTrack.ui.theme.DeepBlue

@Composable
fun GoalScreen(
    viewModel: StepCounterViewModel = viewModel(),
    navController: NavController
) {
    // Håll tillståndet för mål
    var stepGoal = viewModel.stepsGoal.observeAsState().value
    var waterGoal = viewModel.goalHyd.observeAsState().value

    Scaffold(
        backgroundColor = DeepBlue,
        bottomBar = { BottomNavigationBar(navController = navController)}
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Stegmål Card
            if (stepGoal != null) {
                GoalCard(
                    title = "Step Goal",
                    goalValue = stepGoal,
                    unit = "",
                    onIncrease = {
                        stepGoal += 500
                        viewModel.updateUserStepsGoal(stepGoal)
                    },
                    onDecrease = {
                        stepGoal -= 500
                        viewModel.updateUserStepsGoal(stepGoal)
                    },
                    imageRes = R.drawable.baseline_directions_run_24
                )
            }

            // Vattenintagsmål Card
            if (waterGoal != null) {
                GoalCard(
                    title = "Hydration Goal",
                    goalValue = waterGoal,
                    unit = "L",
                    onIncrease = {
                        waterGoal += 0.1
                        viewModel.updateUserWaterIntakeGoal(waterGoal)
                    },
                    onDecrease = {
                        waterGoal -= 0.1
                        viewModel.updateUserWaterIntakeGoal(waterGoal)
                    },
                    imageRes = R.drawable.ic_water_icon
                )
            }
        }
    }
}

@Composable
fun GoalCard(
    title: String,
    goalValue: Number,
    unit: String,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier,
    imageRes: Int
) {
    Card(
        backgroundColor = Color(0xFF1B3B5A),
        shape = MaterialTheme.shapes.small,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
            .clip(MaterialTheme.shapes.small)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDecrease,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colors.surface)
                ) {
                    Icon(Icons.Filled.Clear, contentDescription = "Minska", tint = Color.White)
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "${goalValue.format()} ",
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(16.dp))

                IconButton(
                    onClick = onIncrease,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colors.surface)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Öka", tint = Color.White)
                }
            }
            Row (
                modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
            ){
                Spacer(modifier = Modifier.align(Alignment.CenterVertically))
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier.size(25.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light
                )


            }
        }
    }
}

fun Number.format(): String = when (this) {
    is Int -> this.toString()
    is Double -> String.format("%.1f", this)
    else -> this.toString()
}