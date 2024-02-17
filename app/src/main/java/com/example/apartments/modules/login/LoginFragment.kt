package com.example.apartments.modules.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.Navigation
import com.example.apartments.R
import com.example.apartments.common.RequiredValidation

class LoginFragment : Fragment() {
    private var TAG = "LoginFragment"

    private var emailTextField: EditText? = null
    private var passwordTextField: EditText? = null
    private var loginButton: Button? = null
    private var signUpButton: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        setupUi(view)
        return view
    }

    private fun setupUi(view: View) {
        emailTextField = view.findViewById(R.id.etLoginFragmentEmail)
        passwordTextField = view.findViewById(R.id.etLoginFragmentPassword)
        loginButton = view.findViewById(R.id.btnLoginFragmentLogin)
        signUpButton = view.findViewById(R.id.btnRegisterFragmentSignIn)

        loginButton?.setOnClickListener(::onLoginButtonClicked)
        signUpButton?.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_loginFragment_to_registerFragment))
    }

    private fun onLoginButtonClicked(view: View) {
        val isValidEmail = RequiredValidation.validateRequiredTextField(emailTextField!!, "email")
        val isValidPassword = RequiredValidation.validateRequiredTextField(passwordTextField!!, "password")
        if (isValidEmail && isValidPassword) {
            // todo: firebase login user
            Log.d(TAG, "firebase login")
        }

        Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_appActivity)
    }
}