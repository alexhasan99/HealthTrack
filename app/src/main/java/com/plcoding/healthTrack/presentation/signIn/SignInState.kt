package com.plcoding.healthTrack.presentation.signIn

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInErrorMessage: String? = null,
)
