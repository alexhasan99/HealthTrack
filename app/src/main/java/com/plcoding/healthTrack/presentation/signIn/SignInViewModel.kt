package com.plcoding.healthTrack.presentation.signIn

import androidx.lifecycle.ViewModel
import com.plcoding.healthTrack.data.UserProfile
import com.plcoding.healthTrack.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignInViewModel(private val userRepository: UserRepository): ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state .asStateFlow()

    fun onSignInResult(result: SignInResult) {
        _state.update { it.copy(
            isSignInSuccessful = result.data != null,
            signInErrorMessage = result.errorMessage
        ) }
        result.data?.let { data ->
            checkAndSaveUserProfile(data.userId, data.email, data.username)
        }
    }

    private fun checkAndSaveUserProfile(userId: String, email: String, username: String?) {
        userRepository.userProfileExists(userId) { exists ->
            if (!exists) {
                userRepository.saveUserProfile(UserProfile(userId = userId, email = email, username = username.toString()))
            }
        }
    }

    fun resetState() {
        _state.update { SignInState() }
    }
}