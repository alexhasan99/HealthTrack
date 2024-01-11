package com.plcoding.healthTrack.presentation.mainScreen.circularBar


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.healthTrack.R
import com.plcoding.healthTrack.ui.theme.LightBlue

@Composable
fun MainCircularProgressBar(
    currentSteps: Int,
    goalSteps: Int,
    fontSize: TextUnit = 40.sp,
    radius: Dp,
    color: Color = LightBlue,
    strokeWidth: Dp = 10.dp,
    animDuration: Int = 1000,
    animDelay: Int = 0
    ) {
    var animationPlayed by remember{
        mutableStateOf(false)
    }
    var percentage = currentSteps.toFloat().div(goalSteps.toFloat())
    val curPercentage = animateFloatAsState(
        targetValue = if (animationPlayed) percentage else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = animDelay
        )
    )

    LaunchedEffect(key1 = true){
        animationPlayed = true
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(radius * 2f)
    ) {
        Canvas(modifier = Modifier.size(radius * 2f)) {
            drawArc(
                color = Color.DarkGray,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(3.dp.toPx(), cap = StrokeCap.Round)
            )

            drawArc(
                color = LightBlue,
                -90f,
                360 *  curPercentage.value,
                useCenter = false,
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        Image(painter = painterResource(id = R.drawable.baseline_directions_run_24), 
            contentDescription = null, 
            modifier = Modifier.width(40.dp).offset(y = (-60).dp)
        )
        Text(
            modifier = Modifier.offset(y= (-20).dp),
            text = (currentSteps).toString(),
            color = Color.White,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        )
        Text(
            modifier = Modifier.offset(y= 25.dp),
            text = "Today",
            color = Color.LightGray,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraLight
        )
        Text(
            modifier = Modifier.offset(y= 60.dp),
            text = "Goal $goalSteps",
            color = Color.LightGray,
            fontSize =  15.sp,
            fontWeight = FontWeight.Medium
        )
    }

}