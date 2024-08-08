package com.training.ecommerce.ui.auth.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.training.ecommerce.data.repository.user.UserPreferencesRepository
import kotlinx.coroutines.launch

class LoginViewModel(val userPrefs: UserPreferencesRepository) : ViewModel() {

    fun saveLoginState(isLoggedIn: Boolean) {
        viewModelScope.launch {

        }

    }

}