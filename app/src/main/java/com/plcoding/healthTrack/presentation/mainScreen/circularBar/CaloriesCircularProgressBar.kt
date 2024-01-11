package com.plcoding.healthTrack.presentation.mainScreen.circularBar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.healthTrack.R
import com.plcoding.healthTrack.ui.theme.LightBlue

@Composable
fun CaloriesCircularProgressBar(
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
    var percentage = 0.7f
    val safePercentage = if (percentage.isNaN()) 0f else percentage
    val curPercentage = animateFloatAsState(
        targetValue = if (animationPlayed) safePercentage else 0f,
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
        Image(painter = painterResource(id = R.drawable.ic_calories_icon),
            contentDescription = null,
            modifier = Modifier.width(40.dp)
        )
    }

}

@Composable
fun SafeAnimateCircularProgress(targetProgress: Float) {
    val progress by animateFloatAsState(
        targetValue = if (targetProgress.isNaN()) 0f else targetProgress,
        animationSpec = tween(durationMillis = 1000)
    )

    // Your progress bar drawing logic using 'progress'
}