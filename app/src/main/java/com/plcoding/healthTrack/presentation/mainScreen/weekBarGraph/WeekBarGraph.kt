package com.plcoding.healthTrack.presentation.mainScreen.weekBarGraph

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.plcoding.healthTrack.data.DailyActivity
import com.plcoding.healthTrack.ui.theme.BlueCustom
import com.plcoding.healthTrack.ui.theme.LightBlue
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun WeekBarGraph(weekPrognosis: List<DailyActivity>) {
    val graphBarData = weekPrognosis.map { it.dailyStep.toFloat() }
    val xAxisScaleData = weekPrognosis.map { dailyActivity ->
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dailyActivity.date)
        val dayOfWeek = SimpleDateFormat("EEE", Locale.ENGLISH).format(date)
        dayOfWeek
    }

    val height = 120.dp
    val barWidth = 15.dp
    val barColor = LightBlue
    val maxYAxisValue = graphBarData.maxOrNull() ?: 0f
    val normalizedBarData = graphBarData.map { it / maxYAxisValue }

    // Y-Axis levels
    val yAxisLevels = calculateYAxisLevels(maxYAxisValue)

    Column(modifier = Modifier.padding(10.dp)) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .clip(MaterialTheme.shapes.small)
                .background(BlueCustom)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Draw the bars
                val barSpacing = (size.width - (barWidth.toPx() * normalizedBarData.size)) / (normalizedBarData.size + 1)
                normalizedBarData.forEachIndexed { index, normalizedValue ->
                    val barHeight = normalizedValue * size.height
                    val left = barSpacing * (index + 1) + barWidth.toPx() * index
                    val top = size.height - barHeight
                    val right = left + barWidth.toPx()

                    drawRect(
                        color = barColor,
                        topLeft = Offset(left, top),
                        size = androidx.compose.ui.geometry.Size(right - left, barHeight)
                    )

                }
                val textPaint = Paint().apply {
                    color = Color.LightGray.hashCode()
                    textSize = 40f // Adjust text size as needed
                    textAlign = Paint.Align.LEFT
                }

                // Draw Y-Axis labels and horizontal lines
                yAxisLevels.forEach { level ->
                    val yPos = size.height * (1f - (level / maxYAxisValue))
                    drawLine(
                        color = androidx.compose.ui.graphics.Color.Gray,
                        start = Offset(0f, yPos),
                        end = Offset(size.width, yPos)
                    )

                    // Draw the Y-axis label text
                    drawContext.canvas.nativeCanvas.drawText(
                        level.toInt().toString(),
                        6f, // x-coordinate
                        yPos - 5f, // y-coordinate, adjust as needed
                        textPaint
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            xAxisScaleData.forEach { date ->
                Text(
                    text = date,
                    fontSize = 12.sp,
                    color = Color.White,
                    modifier = Modifier.padding(2.dp)
                )
            }
        }
    }
}

fun calculateYAxisLevels(maxSteps: Float): List<Float> {
    // Ensure maxSteps is positive to avoid IllegalArgumentException
    val safeMaxSteps = if (maxSteps > 0) maxSteps else 1f
    val step = safeMaxSteps / 4  // Adjust the division as per your requirement

    return (0..4).map { it * step }
}

