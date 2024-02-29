package com.example.apartments.modules.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.apartments.R
import com.example.apartments.base.MyApplication
import com.example.apartments.common.RequiredValidation
import com.example.apartments.databinding.FragmentLoginBinding
import com.example.apartments.model.auth.AuthModel
import com.example.apartments.modules.register.RegisterFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment() {
    companion object {
        const val TAG = "LoginFragment"
    }

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var emailTextField: EditText
    private lateinit var passwordTextField: EditText
    private lateinit var loginButton: Button
    private lateinit var signUpButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        setupUi()

        return binding.root
    }

    private fun setupUi() {
        emailTextField = binding.etLoginFragmentEmail
        passwordTextField = binding.etLoginFragmentPassword
        loginButton = binding.btnLoginFragmentLogin
        signUpButton = binding.btnLoginFragmentSignUp

        loginButton.setOnClickListener(::onLoginButtonClicked)
        signUpButton.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_loginFragment_to_registerFragment))
    }

    private fun onLoginButtonClicked(view: View) {
        val isValidEmail = RequiredValidation.validateRequiredTextField(emailTextField, "email")
        val isValidPassword = RequiredValidation.validateRequiredTextField(passwordTextField, "password")
        if (isValidEmail && isValidPassword) {
            Log.d(TAG, "firebase login")
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val authResult = AuthModel.instance.signIn(emailTextField.text.toString(), passwordTextField.text.toString())

                    withContext(Dispatchers.Main) {
                        Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_appActivity)
                    }
                } catch (e: Exception) {
                    Log.e(RegisterFragment.TAG, "An unexpected error occurred: ${e.message}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            MyApplication.Globals.appContext,
                            "some of the login details are incorrect",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
            }
        } else {
            Log.e(TAG, "some of the login details are missing")
            Toast.makeText(
                MyApplication.Globals.appContext,
                "missing some login details",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}