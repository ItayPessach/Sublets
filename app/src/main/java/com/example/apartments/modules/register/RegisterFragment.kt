package com.example.apartments.modules.register

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.apartments.R
import com.example.apartments.base.MyApplication
import com.example.apartments.common.FirebaseStorageModel
import com.example.apartments.common.RequiredValidation
import com.example.apartments.databinding.FragmentRegisterBinding
import com.example.apartments.model.auth.AuthModel
import com.example.apartments.model.user.User
import com.example.apartments.model.user.UserModel
import com.example.apartments.modules.login.LoginFragment
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class RegisterFragment : Fragment() {
    companion object {
        const val TAG = "RegisterFragment"
    }

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var userImageView: ImageView
    private lateinit var nameTextField: EditText
    private lateinit var emailTextField: EditText
    private lateinit var passwordTextField: EditText
    private lateinit var phoneNumberTextField: EditText
    private lateinit var addImageBtn: Button
    private lateinit var registerButton: Button
    private lateinit var signInButton: Button
    private var avatarUri: Uri? = null

    private val addImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImageUri = result.data?.data

            userImageView.setImageURI(selectedImageUri)
            avatarUri = selectedImageUri
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        setupUi()

        return binding.root
    }

    private fun setupUi() {
        userImageView = binding.ivRegisterFragmentUserAvatar
        nameTextField = binding.etRegisterFragmentName
        emailTextField = binding.etRegisterFragmentEmail
        passwordTextField = binding.etRegisterFragmentPassword
        phoneNumberTextField = binding.etRegisterFragmentPhoneNumber
        addImageBtn = binding.btnRegisterFragmentAddImage
        registerButton = binding.btnRegisterFragmentRegister
        signInButton = binding.btnRegisterFragmentSignIn

        addImageBtn.setOnClickListener(::onAddImageButtonClicked)
        registerButton.setOnClickListener(::onRegisterButtonClicked)
        signInButton.setOnClickListener(::onSignInButtonClicked)
    }

    private fun onAddImageButtonClicked(view: View) {
        val imagePickerIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        addImageLauncher.launch(imagePickerIntent)
    }

    private fun onRegisterButtonClicked(view: View) {
        val isValidName = RequiredValidation.validateRequiredTextField(nameTextField, "name")
        val isValidEmail = RequiredValidation.validateRequiredTextField(emailTextField, "email")
        val isValidPassword = RequiredValidation.validateRequiredTextField(passwordTextField, "password")
        val isValidPhoneNumber = RequiredValidation.validateRequiredTextField(phoneNumberTextField, "phone number")
        val isValidPhoto = avatarUri != null

        if (isValidName && isValidEmail && isValidPassword && isValidPhoneNumber && isValidPhoto) {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val authResult = AuthModel.instance.signUp(emailTextField.text.toString(), passwordTextField.text.toString())
                    val userId = authResult.user!!.uid
                    val avatarUrl = FirebaseStorageModel.instance.addImageToFirebaseStorage(avatarUri!!, FirebaseStorageModel.USERS_PATH)
                    val user = User(userId, nameTextField.text.toString(),phoneNumberTextField.text.toString(), emailTextField.text.toString(), avatarUrl)
                    UserModel.instance.addUser(user)
                    withContext(Dispatchers.Main) {
                        Navigation.findNavController(view).popBackStack(R.id.loginFragment, false)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "An unexpected error occurred: ${e.message}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            MyApplication.Globals.appContext,
                            e.message,
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
            }
        } else {
            Log.e(TAG, "some of the registration details are missing")
            Toast.makeText(
                MyApplication.Globals.appContext,
                "missing some registration details",
                Toast.LENGTH_SHORT,
            ).show()
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