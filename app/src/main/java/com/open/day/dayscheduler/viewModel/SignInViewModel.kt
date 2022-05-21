package com.open.day.dayscheduler.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.open.day.dayscheduler.data.repository.TaskRepository
import com.open.day.dayscheduler.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    fun updateUserWithGoogleAuth(signInAccount: GoogleSignInAccount) {
        viewModelScope.launch {
            val userModel = userRepository.getLocalUser()
            userModel.name = signInAccount.givenName
            userModel.surname = signInAccount.familyName
            userRepository.updateUser(userModel)
        }
    }
}