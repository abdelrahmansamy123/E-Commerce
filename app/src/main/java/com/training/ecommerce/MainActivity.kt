package com.training.ecommerce

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        initialSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
}