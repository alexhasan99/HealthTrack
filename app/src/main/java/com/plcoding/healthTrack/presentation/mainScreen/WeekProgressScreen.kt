package com.plcoding.healthTrack.presentation.mainScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
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
import com.plcoding.healthTrack.data.DailyActivity
import com.plcoding.healthTrack.ui.theme.BlueCustom
import com.plcoding.healthTrack.ui.theme.DeepBlue
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun WeekProgressScreen(
    viewModel: StepCounterViewModel = viewModel(),
    navController: NavController
) {
    val weekPrognosis = viewModel.weekPrognosis.observeAsState().value ?: listOf()
    Scaffold(
        backgroundColor = DeepBlue,
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) {
        LazyColumn(
            modifier = Modifier.padding(it)
        ) {
            items(weekPrognosis) { dailyActivity ->
                DailyActivityCard(dailyActivity)
            }
        }
    }

}

@Composable
fun DailyActivityCard(dailyActivity: DailyActivity) {
    Card(
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp, horizontal = 20.dp)
            .clip(MaterialTheme.shapes.small),
        elevation = 4.dp,
        backgroundColor = BlueCustom
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .background(BlueCustom)
        ) {
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dailyActivity.date)
            val formattedDate = SimpleDateFormat("MM/dd", Locale.getDefault()).format(date)
            val dayOfWeek = SimpleDateFormat("EEE", Locale.ENGLISH).format(date)

            DailyRow(
                string = "$formattedDate $dayOfWeek",
                imageRes = R.drawable.twotone_calendar_month_24,
                modifier = Modifier)
            DailyRow(
                string = dailyActivity.dailyStep.toString() + " steps",
                imageRes = R.drawable.baseline_directions_run_24,
                modifier = Modifier)
            DailyRow(
                string = dailyActivity.calories.toString() + " Kcal",
                imageRes = R.drawable.ic_calories_icon,
                modifier = Modifier)
            DailyRow(
                string = dailyActivity.hydration.toString(),
                imageRes = R.drawable.ic_water_icon,
                modifier = Modifier)
        }
    }
}

@Composable
fun DailyRow(
    string: String,
    imageRes: Int,
    modifier: Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
    )
    {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.size(19.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = string,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Light
        )
    }
}