package com.example.apartments.modules.addApartment

import android.net.http.HttpException
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresExtension
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.apartments.R
import com.example.apartments.common.RequiredValidation
import com.example.apartments.databinding.FragmentAddApartmentBinding
import com.example.apartments.model.apartment.Apartment
import com.example.apartments.model.apartment.ApartmentModel
import com.example.apartments.model.apartment.ApartmentType
import com.example.apartments.retrofit.MovieSearchResult
import com.example.apartments.retrofit.MoviesSingelton
import com.example.apartments.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddApartmentFragment : Fragment() {
    private var TAG = "AddApartmentFragment"

    private var titleTextField: EditText? = null
    private var descriptionTextField: EditText? = null
    private var numOfRoomsTextField: EditText? = null
    private var addApartmentBtn: Button? = null

    private var _binding: FragmentAddApartmentBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddApartmentBinding.inflate(inflater, container, false)
        setupUi()

        return binding.root
    }

    private fun setupUi() {
        titleTextField = binding.etAddApartmentFragmentTitle
        descriptionTextField = binding.etAddApartmentFragmentDescription
        numOfRoomsTextField = binding.etAddApartmentFragmentNumOfRooms
        addApartmentBtn = binding.btnAddApartmentFragmentAdd

        addApartmentBtn?.setOnClickListener(::onAddApartmentButtonClicked)
    }

    private fun onAddApartmentButtonClicked(view: View) {
        val isValidTitle = RequiredValidation.validateRequiredTextField(titleTextField!!, "email")
        val isValidDescription = RequiredValidation.validateRequiredTextField(descriptionTextField!!, "password")
        val isValidNumOfRooms = RequiredValidation.validateRequiredTextField(numOfRoomsTextField!!, "numOfRooms")

        if (isValidTitle && isValidDescription && isValidNumOfRooms) {
            Log.d(TAG, "firebase create apartment document")
            val title = titleTextField!!.text.toString()
            val description = descriptionTextField!!.text.toString()
            val numOfRooms = numOfRoomsTextField!!.text.toString().toInt()

            val apartment = Apartment("", title, description, "", ApartmentType.Private, numOfRooms)

            ApartmentModel.instance.addApartment(apartment) {
                Navigation.findNavController(view).popBackStack(R.id.apartmentsFragment, false)
            }
        }
    }
}