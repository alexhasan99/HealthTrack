package com.plcoding.healthTrack.presentation.mainScreen.hydrationComponents

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.plcoding.healthTrack.ui.theme.DarkBlue
import com.plcoding.healthTrack.ui.theme.DeepBlue

@Composable
fun WaterIntakeDialog(
    showDialog: MutableState<Boolean>,
    onAmountEntered: (Double) -> Unit
) {
    if (showDialog.value) {
        Dialog(onDismissRequest = { showDialog.value = false }) {
            Card(elevation = 8.dp,
                backgroundColor = DeepBlue) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Enter the amount of water", style = MaterialTheme.typography.h6, color = Color.White)
                    var input by remember { mutableStateOf("") }

                    TextField(
                        value = input,
                        onValueChange = { input = it },
                        label = { Text("Liter") },
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = TextFieldDefaults.textFieldColors(
                            textColor = Color.White,
                            cursorColor = Color.White,
                            leadingIconColor = Color.White,
                            trailingIconColor = Color.White,
                            focusedIndicatorColor = Color.White,
                            unfocusedIndicatorColor = Color.White,
                            disabledIndicatorColor = Color.White,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row {
                        Button(onClick = { showDialog.value = false },
                            colors = ButtonDefaults.buttonColors(backgroundColor = DarkBlue)) {
                            Text("Cancel", color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                showDialog.value = false
                                onAmountEntered(input.toDoubleOrNull() ?: 0.0)
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = DarkBlue)
                        ) {
                            Text("Save", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}