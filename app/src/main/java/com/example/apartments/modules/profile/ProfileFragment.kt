package com.example.apartments.modules.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.apartments.MainActivity
import com.example.apartments.R
import com.example.apartments.base.MyApplication
import com.example.apartments.common.RequiredValidation
import com.example.apartments.databinding.FragmentProfileBinding
import com.example.apartments.model.auth.AuthModel
import com.example.apartments.model.user.UpdateUserInput
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

class ProfileFragment : Fragment() {
    companion object {
        private const val TAG = "ProfileFragment"
    }

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfileViewModel

    private lateinit var avatarImageButton: ImageButton
    private lateinit var nameTextField: EditText
    private lateinit var phoneNumberTextField: EditText
    private lateinit var editButton: Button
    private lateinit var logoutButton: FloatingActionButton
    private lateinit var progressBar: ProgressBar
    private lateinit var layout: View
    private var avatarUri: Uri? = null

    private val addImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImageUri = result.data?.data

            avatarImageButton.setImageURI(selectedImageUri)
            avatarUri = selectedImageUri
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        setupUi()

        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        viewModel.getCurrentUser()

        viewModel.user.observe(viewLifecycleOwner) { user ->
            progressBar.visibility = View.VISIBLE

            nameTextField.setText(user.name)
            phoneNumberTextField.setText(user.phoneNumber)

            // Load image into ImageView using Picasso
            Picasso.get()
                .load(user.avatarUrl)
                .placeholder(R.drawable.account_circle)
                .into(object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        progressBar.visibility = View.GONE
                        layout.visibility = View.VISIBLE
                        avatarImageButton.setImageBitmap(bitmap)
                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                        Log.e(TAG, "error")
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                        Log.d(TAG, "onPrepareLoad")
                    }
                })
        }

        return binding.root
    }

    private fun setupUi() {
        layout = binding.clProfileFragment
        avatarImageButton = binding.ivbProfileFragmentAvatar
        nameTextField = binding.etProfileFragmentName
        phoneNumberTextField = binding.etProfileFragmentPhoneNumber
        editButton = binding.btnProfileFragmentEdit
        logoutButton = binding.fabProfileFragmentLogout
        progressBar = binding.progressBarProfileFragment

        avatarImageButton.setOnClickListener(::onImageViewButton)
        editButton.setOnClickListener(::onEditButtonClicked)
        logoutButton.setOnClickListener(::onLogoutButtonClicked)
    }

    private fun onEditButtonClicked(view: View) {
        val isValidName = RequiredValidation.validateRequiredTextField(nameTextField, "name")
        val isValidEmail = RequiredValidation.validateRequiredTextField(phoneNumberTextField, "phone number")

        if (isValidName && isValidEmail) {
            var avatarUrl = viewModel.user.value!!.avatarUrl
            val name = nameTextField.text.toString()
            val phoneNumber = phoneNumberTextField.text.toString()


            val updateUserInput = UpdateUserInput(name, phoneNumber, avatarUrl)
            viewModel.updateCurrentUser(updateUserInput, avatarUri)
            Toast.makeText(
                MyApplication.Globals.appContext,
                "details updated",
                Toast.LENGTH_SHORT,
            ).show()
        } else {
            Log.e(TAG, "missing some user details")
            Toast.makeText(
                MyApplication.Globals.appContext,
                "you must fill all the fields",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    private fun onImageViewButton(view: View) {
        val imagePickerIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        addImageLauncher.launch(imagePickerIntent)
    }

    private fun onLogoutButtonClicked(view: View) {
        AuthModel.instance.signOut()
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}
