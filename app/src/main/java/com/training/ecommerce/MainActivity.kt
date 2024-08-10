package com.training.ecommerce

import android.animation.ObjectAnimator
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.training.ecommerce.data.dataSource.dataStore.UserPreferencesDataSource
import com.training.ecommerce.data.repository.user.UserDataStoreRepositoryImpl
import com.training.ecommerce.ui.auth.AuthActivity
import com.training.ecommerce.ui.common.viewModel.UserViewModel
import com.training.ecommerce.ui.common.viewModel.UserViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(UserDataStoreRepositoryImpl(UserPreferencesDataSource(this)))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initialSplashScreen()
        super.onCreate(savedInstanceState)

        runBlocking {
            val isLoggedIn = userViewModel.isUserLoggedIn().first()
            Log.d(TAG, "onCreate: isLoggedIn: $isLoggedIn")
            if (isLoggedIn) {
                setContentView(R.layout.activity_main)
            } else {
                goToAuthActivity()
            }


        }
        Log.d(TAG, "onCreate: ")

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

    private fun initialSplashScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            installSplashScreen()
            //Add a callback that's called when the splash screen is animating to the
            //app content
            splashScreen.setOnExitAnimationListener { splashScreenView ->

                //create your custom animation
                val slideUp = ObjectAnimator.ofFloat(
                    splashScreenView,
                    View.TRANSLATION_Y,
                    0f,
                    -splashScreenView.height.toFloat()
                )
                slideUp.interpolator = AnticipateInterpolator()
                slideUp.duration = 2000L

                //call splashScreenView.remove at the end of your custom animation
                slideUp.doOnEnd { splashScreenView.remove() }

                //Run your animation
                slideUp.start()

            }

        } else {
            setTheme(R.style.Theme_ECommerce)
        }
    }

    private fun goToAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val options = ActivityOptions.makeCustomAnimation(
            this, android.R.anim.fade_in, android.R.anim.fade_out
        )
        startActivity(intent, options.toBundle())
        finish()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}