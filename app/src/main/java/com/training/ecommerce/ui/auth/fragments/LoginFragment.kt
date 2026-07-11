package com.training.ecommerce.ui.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.training.ecommerce.R
import com.training.ecommerce.data.models.Resource
import com.training.ecommerce.databinding.FragmentLoginBinding
import com.training.ecommerce.ui.auth.getGoogleRequestIntent
import com.training.ecommerce.ui.auth.viewModel.LoginViewModel
import com.training.ecommerce.ui.auth.viewModel.LoginViewModelFactory
import com.training.ecommerce.ui.common.views.ProgressDialog
import com.training.ecommerce.ui.home.MainActivity
import com.training.ecommerce.ui.showSnakeBarError
import com.training.ecommerce.utils.CrashlyticsUtils
import com.training.ecommerce.utils.LoginException
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {

    private val callbackManager: CallbackManager by lazy { CallbackManager.Factory.create() }
    private val loginManager: LoginManager by lazy { LoginManager.getInstance() }

    val progressDialog by lazy { ProgressDialog.createProgressDialog(requireActivity()) }

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(contextValue = requireContext())
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = loginViewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        initViewModel()
    }

    private fun initListeners() { // Done
        binding.googleBtn.setOnClickListener {
            loginWithGoogleRequest()
        }
        binding.facebookBtn.setOnClickListener {
            loginWithFacebook()
        }
        binding.registerTv.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
//        binding.forgotPasswordTv.setOnClickListener {
//            val forgetPasswordFragment = ForgetPasswordFragment()
//            forgetPasswordFragment.show(parentFragmentManager, "forget-password")
//        }
    }

    //ActivityResultLauncher for the sign-in intent
    private val launcher =  // Done
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
            } else {
                view?.showSnakeBarError(getString(R.string.google_sign_in_failed_msg))
            }
        }


    private fun loginWithGoogleRequest() {
        val signInIntent = getGoogleRequestIntent(requireActivity())
        launcher.launch(signInIntent)
    }


    private fun firebaseAuthWithFacebook(idToken: String) {
        loginViewModel.loginWithFacebook(idToken)   // Done
    }


    private fun loginWithFacebook() {  // Done
        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val token = result.accessToken.token
                Log.d(TAG, "onSuccess: $token")
                firebaseAuthWithFacebook(token)
            }
            override fun onCancel() {
                TODO("Not yet implemented")
            }

            override fun onError(error: FacebookException) {
                val msg = error.message ?: getString(R.string.generic_error_msg)
                Log.d(TAG, "onError: $msg")
                view?.showSnakeBarError(msg)
                logAuthIssueToCrashlytics(msg, "Facebook")
            }
        })
        loginManager.logInWithReadPermissions(
            this,
            callbackManager,
            listOf("email", "pubic_profile")
        )


    }


    private fun handleSignInResult(completeTask: Task<GoogleSignInAccount>) { // Done
        try {
            val account = completeTask.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken!!)

        } catch (e: Exception) {
            //Sign in was unsuccessfully
            view?.showSnakeBarError(e.message ?: getString(R.string.generic_error_msg))
            val msg = e.message ?: getString(R.string.generic_error_msg)
            logAuthIssueToCrashlytics(msg, "Google")

        }
    }

    private fun logAuthIssueToCrashlytics(msg: String, provider: String) { // Done
        CrashlyticsUtils.sendCustomLogToCrashlytics<LoginException>(
            msg,
            CrashlyticsUtils.LOGIN_KEY to msg,
            CrashlyticsUtils.LOGIN_PROVIDER to provider
        )
    }


    private fun firebaseAuthWithGoogle(idToken: String) { // Done
        loginViewModel.loginWithGoogle(idToken)
    }


    private fun initViewModel() {  // Done
        lifecycleScope.launch {
            loginViewModel.loginState.collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        progressDialog.show()
                    }

                    is Resource.Success -> {
                        progressDialog.dismiss()
                        goToHome()
                    }

                    is Resource.Error -> {
                        progressDialog.dismiss()
                        val msg =
                            resource.exception?.message ?: getString(R.string.generic_error_msg)
                        Log.d(TAG, "initViewModelError: $msg")
                        view?.showSnakeBarError(msg)
                    }
                }
            }
        }
    }

    private fun goToHome() { // Done
        requireActivity().startActivity(Intent(activity, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        requireActivity().finish()
    }

    override fun onDestroyView() {
        super.onDestroyView() // Done
        _binding = null
    }

    private fun isLoggedIn(): Boolean {  // Done
        val accessToken = AccessToken.getCurrentAccessToken()
        return accessToken != null && !accessToken.isExpired
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        callbackManager.onActivityResult(resultCode, resultCode, data) // Done
//    }

    private fun signOut() {  // Done
        loginManager.logOut()
        Log.d(TAG, "signOut: ")
    }

    companion object {
        private const val TAG = "LoginFragment"  // Done
    }


}