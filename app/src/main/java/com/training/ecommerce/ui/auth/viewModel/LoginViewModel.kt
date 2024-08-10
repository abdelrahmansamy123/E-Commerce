package com.training.ecommerce.ui.auth.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.training.ecommerce.data.repository.auth.FirebaseAuthRepository
import com.training.ecommerce.data.repository.user.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    val userPrefs: UserPreferencesRepository,
    val authFirebaseAuthRepository: FirebaseAuthRepository
) : ViewModel() {


    val email = MutableStateFlow("")
    val password = MutableStateFlow("")


    companion object {
        private const val TAG = "LoginViewModel"
    }


    fun login() {
        viewModelScope.launch {
            val email = email.value
            val password = password.value
            if (email.isNotEmpty() && password.isNotEmpty()) {

            }
        }
    }
}

class LoginViewModelFactory(
    private val userPrefs: UserPreferencesRepository,
    private val authFirebaseAuthRepository: FirebaseAuthRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return LoginViewModel(
                userPrefs,
                authFirebaseAuthRepository
            ) as T
        }
        throw IllegalAccessException("Unknown viewModel class ")
    }
}
