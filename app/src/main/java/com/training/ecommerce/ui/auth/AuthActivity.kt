package com.training.ecommerce.ui.auth

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.training.ecommerce.R
import com.training.ecommerce.ui.auth.viewModel.AuthViewModel

class AuthActivity : AppCompatActivity() {

    val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
    }
}