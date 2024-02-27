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
import com.example.apartments.common.RequiredValidation
import com.example.apartments.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {
    private var TAG = "RegisterFragment"

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private var nameTextField: EditText? = null
    private var emailTextField: EditText? = null
    private var passwordTextField: EditText? = null
    private var phoneNumberTextField: EditText? = null
    private var registerButton: Button? = null
    private var signInButton: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        setupUi()

        return binding.root
    }

    private fun setupUi() {
        nameTextField = binding.etRegisterFragmentName
        emailTextField = binding.etRegisterFragmentEmail
        passwordTextField = binding.etRegisterFragmentPassword
        registerButton = binding.btnRegisterFragmentRegister
        signInButton = binding.btnRegisterFragmentSignIn
        phoneNumberTextField = binding.etRegisterFragmentPhoneNumber

        registerButton?.setOnClickListener(::onRegisterButtonClicked)
        signInButton?.setOnClickListener(::onSignInButtonClicked)
    }

    private fun onRegisterButtonClicked(view: View) {
        val isValidName = RequiredValidation.validateRequiredTextField(nameTextField!!, "name")
        val isValidEmail = RequiredValidation.validateRequiredTextField(emailTextField!!, "email")
        val isValidPassword = RequiredValidation.validateRequiredTextField(passwordTextField!!, "password")
        val isValidPhoneNumber = RequiredValidation.validateRequiredTextField(phoneNumberTextField!!, "phone number")

        if (isValidName && isValidEmail && isValidPassword && isValidPhoneNumber) {
            // todo: firebase login user
            Log.d(TAG, "firebase create user")
        }
    }

    private fun onSignInButtonClicked(view: View) {
        Navigation.findNavController(view).popBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}