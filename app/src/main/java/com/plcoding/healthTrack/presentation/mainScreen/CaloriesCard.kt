package com.plcoding.healthTrack.presentation.mainScreen

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
import com.plcoding.healthTrack.presentation.mainScreen.circularBar.CaloriesCircularProgressBar

@Composable
fun CaloriesCard(
    modifier: Modifier,
    currentCalories: Int
){
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
       CaloriesCircularProgressBar(radius = 40.dp)
        Text(modifier = Modifier.offset(y= 10.dp),text = "$currentCalories Kcal", color = Color.White, fontWeight = FontWeight.Bold)
    }
}