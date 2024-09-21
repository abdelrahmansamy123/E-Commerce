package com.training.ecommerce.ui.auth.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class AuthViewModel : ViewModel() {
    val useId = MutableStateFlow("")
}