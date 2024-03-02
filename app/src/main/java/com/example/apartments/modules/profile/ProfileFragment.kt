package com.example.apartments.modules.profile

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import com.example.apartments.MainActivity
import com.example.apartments.R
import com.example.apartments.databinding.FragmentProfileBinding
import com.example.apartments.model.auth.AuthModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.lang.Exception

class ProfileFragment : Fragment() {
    companion object {
        private const val TAG = "ProfileFragment"
    }

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ProfileViewModel

    private lateinit var avatarImageView: ImageView
    private lateinit var nameTextField: EditText
    private lateinit var phoneNumberTextField: EditText
    private lateinit var editButton: Button
    private lateinit var logoutButton: FloatingActionButton
    private lateinit var progressBar: ProgressBar
    private lateinit var layout: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        setupUi()

        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        viewModel.getUser(AuthModel.instance.getUser()!!.uid)

        viewModel.user.observe(viewLifecycleOwner) { user ->
            progressBar.visibility = View.VISIBLE

            nameTextField.setText(user.name)
            phoneNumberTextField.setText(user.phoneNumber)

            Picasso.get().invalidate(user.avatarUrl);

            // Load image into ImageView using Picasso
            Picasso.get()
                .load(user.avatarUrl)
                .placeholder(R.drawable.account_circle)
                .into(object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        progressBar.visibility = View.GONE
                        layout.visibility = View.VISIBLE
                        val resizedBitmap = Bitmap.createScaledBitmap(bitmap!!, 800, 680, false)
                        avatarImageView.setImageBitmap(resizedBitmap)
                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                        Log.e(TAG, "erorr")
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                        Log.d(TAG, "onPrepareLoad")
                    }
                })
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun setupUi() {
        layout = binding.clProfileFragment
        avatarImageView = binding.ivProfileFragmentAvatar
        nameTextField = binding.etProfileFragmentName
        phoneNumberTextField = binding.etProfileFragmentPhoneNumber
        editButton = binding.btnProfileFragmentEdit
        logoutButton = binding.fabProfileFragmentLogout
        progressBar = binding.progressBarProfileFragment

        logoutButton.setOnClickListener(::onLogoutButtonClicked)
    }

    private fun onLogoutButtonClicked(view: View) {
        Log.d(TAG, "${AuthModel.instance.getUser()}")
        AuthModel.instance.signOut()
        Log.d(TAG, "${AuthModel.instance.getUser()}")
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}
