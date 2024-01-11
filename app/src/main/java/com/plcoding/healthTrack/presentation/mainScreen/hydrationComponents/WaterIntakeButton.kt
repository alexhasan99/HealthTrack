package com.plcoding.healthTrack.presentation.mainScreen.hydrationComponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.plcoding.healthTrack.R
import com.plcoding.healthTrack.ui.theme.DarkBlue


@Composable
fun WaterIntakeButton(
    modifier: Modifier,
    onWaterIntake: (Double) -> Unit,
    shape: Shape = CircleShape,
    buttonColor: Color = DarkBlue
) {
    val showDialog = remember { mutableStateOf(false) }

    Button(
        onClick = {
            showDialog.value = true
        },
        modifier = modifier,
        shape = shape,
        colors = ButtonDefaults.buttonColors(backgroundColor = buttonColor)
    ) {
        // Icon and plus sign inside the button
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = R.drawable.ic_water_icon),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.ic_plus_icone),
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 14.dp)
            )
        }
    }
    WaterIntakeDialog(showDialog = showDialog, onAmountEntered = onWaterIntake)
}