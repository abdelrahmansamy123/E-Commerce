package com.training.ecommerce.ui.auth.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.training.ecommerce.data.models.Resource
import com.training.ecommerce.data.repository.auth.FirebaseAuthRepository
import com.training.ecommerce.data.repository.user.UserPreferencesRepository
import com.training.ecommerce.utils.isValidEmail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class LoginViewModel(
    val userPrefs: UserPreferencesRepository,
    val authFirebaseAuthRepository: FirebaseAuthRepository
) : ViewModel() {

    private val _loginState = MutableSharedFlow<Resource<String>>()
    val loginState: SharedFlow<Resource<String>> = _loginState.asSharedFlow()

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
                        when (resource) {
                            is Resource.Success -> {

                                _loginState.emit(Resource.Success(resource.data ?: "Empty User ID"))
                            }

                            else -> _loginState.emit(resource)
                        }
                    }.launchIn(viewModelScope)
            } else {
                _loginState.emit(Resource.Error(Exception("Invalid email or password")))
            }
        }
    }

    fun loginWithGoogle(idToken: String) = viewModelScope.launch {
        authFirebaseAuthRepository.loginWithGoogle(idToken).onEach { res ->
            when (res) {
                is Resource.Success -> {
                    _loginState.emit(Resource.Success(res.data ?: "Empty User ID"))
                }

                else -> _loginState.emit(res)
            }
        }.launchIn(viewModelScope)

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

