package com.open.day.dayscheduler.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.open.day.dayscheduler.data.repository.UserRepository
import com.open.day.dayscheduler.model.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    fun updateUserWithGoogleAuth(account: GoogleSignInAccount) {
        viewModelScope.launch {
            val userModel = userRepository.getLocalUser()

            userRepository.updateUser(UserModel(
                userModel.id, account.givenName, account.familyName, userModel.isLocalUser,
                true, account.email, account.id, account.photoUrl.toString()
            ))
        }
    }
}