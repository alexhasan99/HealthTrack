package com.plcoding.healthTrack.presentation.mainScreen.hydrationComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.plcoding.healthTrack.presentation.mainScreen.circularBar.WaterCircularProgressBar

@Composable
fun HydrationCard(
    modifier: Modifier,
    currentHyd: Double?,
    goalHyd: Double?
) {
    val safeCurrentHyd = currentHyd?.takeIf { it > 0 } ?: 1.0
    val safeGoalHyd = goalHyd?.takeIf { it > 0 } ?: 1.0

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        WaterCircularProgressBar(
            currentHyd = safeCurrentHyd,
            goalHyd = safeGoalHyd,
            radius = 40.dp
        )

        Text(
            modifier = Modifier.offset(y = 10.dp),
            text = "${safeCurrentHyd.format(1)} L",  // Format to one decimal place
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)
