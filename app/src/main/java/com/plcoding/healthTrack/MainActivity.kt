package com.plcoding.healthTrack

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.plcoding.healthTrack.data.UserRepository
import com.plcoding.healthTrack.presentation.mainScreen.GoalScreen
import com.plcoding.healthTrack.presentation.mainScreen.LeaderBoard
import com.plcoding.healthTrack.presentation.mainScreen.MainScreen
import com.plcoding.healthTrack.presentation.mainScreen.StepCounterViewModel
import com.plcoding.healthTrack.presentation.mainScreen.WeekProgressScreen
import com.plcoding.healthTrack.presentation.profile.GoalSettingScreen
import com.plcoding.healthTrack.presentation.profile.ProfileScreen
import com.plcoding.healthTrack.presentation.signIn.GoogleAuthUiClient
import com.plcoding.healthTrack.presentation.signIn.SignInScreen
import com.plcoding.healthTrack.presentation.signIn.SignInViewModel
import com.plcoding.healthTrack.services.BackgroundServices
import com.plcoding.healthTrack.services.StepCounterService
import com.plcoding.healthTrack.ui.theme.HealthTrackTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    val userRepository by lazy {
        UserRepository(FirebaseDatabase.getInstance("https://healthtrack-2024-default-rtdb.europe-west1.firebasedatabase.app/"))
    }

    private var stepCounterService: StepCounterService? = null

    private val fitSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            handleGoogleFitSignInResult(GoogleSignIn.getSignedInAccountFromIntent(result.data))
        }
    }

    private fun requestGoogleFitSignIn() {
        val signInIntent = googleAuthUiClient.getGoogleFitSignInIntent()
        fitSignInLauncher.launch(signInIntent)


    }

    private fun handleGoogleFitSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            // Initialize StepCounterService and ViewModel with this account
            stepCounterService = StepCounterService(applicationContext, account)

            // Optionally, navigate to the step counter screen or refresh UI
        } catch (e: ApiException) {
            Toast.makeText(this, "Failed to sign in to Google Fit", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startBackgroundService() {
        BackgroundServices.stepCounterService = stepCounterService
        BackgroundServices.userRepository = userRepository
        val serviceIntent = Intent(this, BackgroundServices::class.java)
        startService(serviceIntent)
        BackgroundServices.googleAuthUiClient = googleAuthUiClient
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkAndRequestPermissions()
        requestGoogleFitSignIn()


        setContent {
            window.statusBarColor = getColor(R.color.deep_blue)
            HealthTrackTheme {
                val navController = rememberNavController()
                val stepCounterViewModel = StepCounterViewModel(userRepository = userRepository)
                val signInViewModel = SignInViewModel(userRepository)



                NavHost(navController = navController, startDestination = "signInScreen") {

                    composable("signInScreen") {
                        val state by signInViewModel.state.collectAsStateWithLifecycle()


                        LaunchedEffect(key1 = Unit) {
                            if (googleAuthUiClient.getSignedInUser() != null) {
                                navController.navigate("mainScreen")
                            }

                        }
                        val launcher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.StartIntentSenderForResult()
                        ) { result ->
                            if (result.resultCode == RESULT_OK) {
                                lifecycleScope.launch {
                                    val signInResult = googleAuthUiClient.signInWithIntent(
                                        intent = result.data ?: return@launch
                                    )
                                    signInViewModel.onSignInResult(signInResult)
                                }
                            }
                        }


                        LaunchedEffect(key1 = state.isSignInSuccessful) {
                            if (state.isSignInSuccessful) {
                                Toast.makeText(
                                    applicationContext,
                                    "Sign In Successful",
                                    Toast.LENGTH_LONG
                                ).show()
                                navController.navigate("setGoalsScreen")
                                signInViewModel.resetState()
                            }
                        }

                        SignInScreen(
                            state = state,
                            onSignInClick = {
                                lifecycleScope.launch {
                                    val signInIntentSender = googleAuthUiClient.signIn()
                                    launcher.launch(
                                        IntentSenderRequest.Builder(
                                            signInIntentSender ?: return@launch
                                        ).build()
                                    )
                                }
                            }
                        )
                    }

                    composable("setGoalsScreen") {
                        GoalSettingScreen { stepGoal, waterGoal ->
                            stepCounterViewModel.saveGoalSettings(stepGoal, waterGoal)
                            navController.navigate("mainScreen")
                        }
                    }

                    composable("profile") {
                        ProfileScreen(
                            userData = googleAuthUiClient.getSignedInUser(),
                            onSignOut = {
                                lifecycleScope.launch {
                                    googleAuthUiClient.signOut()
                                    Toast.makeText(
                                        applicationContext,
                                        "Signed out",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    navController.popBackStack()
                                }
                            }
                        )
                    }

                    composable("goal") {
                        GoalScreen(
                            viewModel = stepCounterViewModel,
                            navController = navController
                        )
                    }

                    composable("leaderboard") {
                        LeaderBoard(
                            navController = navController,
                            viewModel = stepCounterViewModel,
                            userData = googleAuthUiClient.getSignedInUser()
                        )
                    }

                    composable("weekProgress") {
                        WeekProgressScreen(
                            viewModel = stepCounterViewModel,
                            navController = navController
                        )
                    }


                    composable("mainScreen") {
                        MainScreen(navController, stepCounterViewModel,
                            startService = {
                                Intent(applicationContext, BackgroundServices::class.java).also {
                                    it.action = BackgroundServices.Actions.START.toString()
                                    startService(it)
                                }
                                startBackgroundService()
                            },
                            stopService = {
                                Intent(applicationContext, BackgroundServices::class.java).also {
                                    it.action = BackgroundServices.Actions.STOP.toString()
                                    stopService(it)
                                }
                                stepCounterViewModel.startStepCounting()
                            })
                    }
                }

            }
        }

        lifecycleScope.launch {
            delay(2000) // Delay for 2 seconds
            val serviceIntent = Intent(applicationContext, BackgroundServices::class.java).also {
                it.action = BackgroundServices.Actions.START.toString()
            }
            startService(serviceIntent)
            startBackgroundService() // Additional setup for the service
        }

    }

    private fun checkAndRequestPermissions() {
        val requiredPermissions = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACTIVITY_RECOGNITION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            requiredPermissions.add(android.Manifest.permission.ACTIVITY_RECOGNITION)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requiredPermissions.add(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        if (requiredPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                requiredPermissions.toTypedArray(),
                REQUEST_CODE_PERMISSIONS
            )
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 101
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        HealthTrackTheme {
            Greeting("Android")
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}
