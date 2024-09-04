package com.training.ecommerce.ui.auth.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.training.ecommerce.data.models.Resource
import com.training.ecommerce.data.repository.auth.FirebaseAuthRepository
import com.training.ecommerce.data.repository.user.UserPreferencesRepository
import com.training.ecommerce.utils.isValidEmail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    val userPrefs: UserPreferencesRepository, val authFirebaseAuthRepository: FirebaseAuthRepository
) : ViewModel() {

    val loginState: MutableStateFlow<Resource<String>?> = MutableStateFlow(null)

    val email = MutableStateFlow("")
    val password = MutableStateFlow("")

    private val isLoginIsValid: Flow<Boolean> = combine(email, password) { email, password ->
        email.isValidEmail() && password.length >= 6
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }


    fun login() {
        viewModelScope.launch {
            val email = email.value
            val password = password.value
            if (isLoginIsValid.first()) {
                authFirebaseAuthRepository.loginWithEmailAndPassword(email, password)
                    .onEach { resource ->
                        Log.d(TAG, "Emitted resource: $resource")

                        when (resource) {
                            is Resource.Loading -> loginState.update { Resource.Loading() }
                            is Resource.Success -> {
//                                userPrefs.saveUSerEmail(email)
                                loginState.update {
                                    Resource.Success(
                                        resource.data ?: "Empty User ID"
                                    )
                                }
                            }

                            is Resource.Error -> loginState.value =
                                Resource.Error(resource.exception ?: Exception("Unknown Error"))
                        }
                    }.launchIn(viewModelScope)
            } else {
                loginState.update { Resource.Error(Exception("Invalid email or password ")) }
            }
        }
    }
}

class LoginViewModelFactory(
    private val userPrefs: UserPreferencesRepository,
    private val authFirebaseAuthRepository: FirebaseAuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return LoginViewModel(
                userPrefs, authFirebaseAuthRepository
            ) as T
        }
        throw IllegalAccessException("Unknown viewModel class ")
    }
}
