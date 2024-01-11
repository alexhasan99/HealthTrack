package com.plcoding.healthTrack.presentation.mainScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.plcoding.healthTrack.data.UserStepInfo
import com.plcoding.healthTrack.presentation.signIn.UserData
import com.plcoding.healthTrack.ui.theme.BlueCustom
import com.plcoding.healthTrack.ui.theme.DeepBlue

@Composable
fun LeaderBoard(
    viewModel: StepCounterViewModel = viewModel(),
    navController: NavController,
    userData: UserData?
) {
    viewModel.getUsersSteps()
    val usersSteps = viewModel.usersSteps.observeAsState().value ?: listOf()

    // Sort the list by step count in descending order
    val sortedUsersSteps = usersSteps.sortedByDescending { it.currentSteps }

    Scaffold(
        backgroundColor = DeepBlue,
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) {
        LazyColumn(
            modifier = Modifier.padding(it)
        ) {
            items(sortedUsersSteps) { user ->
                // Check if the current user is the logged-in user
                val isCurrentUser = user.userId == userData?.userId
                UserCard(userStepInfo = user, isCurrentUser = isCurrentUser)
            }
        }
    }
}

@Composable
fun UserCard(userStepInfo: UserStepInfo, isCurrentUser: Boolean) {
    //val cardBackgroundColor = if (isCurrentUser) Color.Green else BlueCustom

    Card(
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 20.dp)
            .clip(MaterialTheme.shapes.small),
        elevation = 4.dp,
        backgroundColor = BlueCustom
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp)
        ) {
            Row (modifier = Modifier.fillMaxWidth()){
                Image(
                    painter = painterResource(R.drawable.ic_person_24),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = " ${userStepInfo.username}",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = if (isCurrentUser) FontWeight.Bold else FontWeight.Light
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row (modifier = Modifier.fillMaxWidth()){
                Image(
                    painter = painterResource(R.drawable.baseline_directions_run_24),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${userStepInfo.currentSteps}",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraLight
                )
            }

        }
    }
}