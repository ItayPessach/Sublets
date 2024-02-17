package com.example.apartments.modules.register

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

class RegisterFragment : Fragment() {
    private var TAG = "RegisterFragment"

    private var nameTextField: EditText? = null
    private var emailTextField: EditText? = null
    private var passwordTextField: EditText? = null
    private var registerButton: Button? = null
    private var signInButton: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        setupUi(view)
        return view
    }

    private fun setupUi(view: View) {
        nameTextField = view.findViewById(R.id.etRegisterFragmentMain)
        emailTextField = view.findViewById(R.id.etRegisterFragmentEmail)
        passwordTextField = view.findViewById(R.id.etRegisterFragmentPassword)
        registerButton = view.findViewById(R.id.btnRegisterFragmentRegister)
        signInButton = view.findViewById(R.id.btnRegisterFragmentSignIn)

        registerButton?.setOnClickListener(::onRegisterButtonClicked)
        signInButton?.setOnClickListener(::onSignInButtonClicked)
    }

    private fun onRegisterButtonClicked(view: View) {
        val isValidName = RequiredValidation.validateRequiredTextField(nameTextField!!, "name")
        val isValidEmail = RequiredValidation.validateRequiredTextField(emailTextField!!, "email")
        val isValidPassword = RequiredValidation.validateRequiredTextField(passwordTextField!!, "password")

        if (isValidName && isValidEmail && isValidPassword) {
            // todo: firebase login user
            Log.d(TAG, "firebase create user")
        }
    }

    private fun onSignInButtonClicked(view: View) {
        Navigation.findNavController(view).popBackStack()
    }
}