package com.example.apartments.modules.addApartment

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.apartments.R
import com.example.apartments.base.MyApplication
import com.example.apartments.common.FirebaseStorageModel
import com.example.apartments.common.RequiredValidation
import com.example.apartments.databinding.FragmentAddApartmentBinding
import com.example.apartments.model.apartment.Apartment
import com.example.apartments.model.apartment.ApartmentModel
import com.example.apartments.model.apartment.ApartmentType
import com.example.apartments.model.auth.AuthModel
import com.example.apartments.modules.register.RegisterFragment
import com.example.apartments.utils.dateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class AddApartmentFragment : Fragment() {
    private var TAG = "AddApartmentFragment"
    private val locations = arrayOf("Tel Aviv - Yafo", "Haifa") // TODO get from external source
    private val types = arrayOf("Apartment", "House", "Villa", "Penthouse")
    private var startDate: Calendar = Calendar.getInstance()
    private var endDate: Calendar = Calendar.getInstance()
    private var imageUri: Uri? = null

    private lateinit var titleTextField: EditText
    private lateinit var descriptionTextField: EditText
    private lateinit var roomsTextField: EditText
    private lateinit var priceTextField: EditText
    private lateinit var locationSelectField: Spinner
    private lateinit var typeSelectField: Spinner
    private lateinit var datesTextView: TextView
    private lateinit var datesBtn: ImageButton
    private lateinit var addImageBtn: ImageButton
    private lateinit var uploadApartmentBtn: Button

    private lateinit var _binding: FragmentAddApartmentBinding
    private val binding get() = _binding

    private val addImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImageUri = result.data?.data

            addImageBtn.setImageURI(selectedImageUri)
            imageUri = selectedImageUri
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddApartmentBinding.inflate(inflater, container, false)
        setupUi()

        return binding.root
    }

    private fun setupUi() {
        titleTextField = binding.etUploadApartmentTitle
        descriptionTextField = binding.etUploadApartmentDescription
        roomsTextField = binding.etUploadApartmentRooms
        priceTextField = binding.etUploadApartmentPrice
        locationSelectField = binding.spUploadApartmentLocation
        typeSelectField = binding.spUploadApartmentType
        datesTextView = binding.tvUploadApartmentDates
        datesBtn = binding.ibUploadApartmentDates
        addImageBtn = binding.ibUploadApartmentAddPhotoButton
        uploadApartmentBtn = binding.btnUploadApartmentUpload

        val locationAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, locations)
        locationAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        locationSelectField.adapter = locationAdapter

        val typeAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, types)
        typeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        typeSelectField.adapter = typeAdapter

        datesBtn.setOnClickListener(::onDatesButtonClicked)
        addImageBtn.setOnClickListener(::onAddImageButtonClicked)
        uploadApartmentBtn.setOnClickListener(::onUploadApartmentButtonClicked)
    }

    private fun onDatesButtonClicked(view: View) {
        setupDatePicker(view, startDate, true, endDate)
    }

    private fun onAddImageButtonClicked(view: View) {
        val imagePickerIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        addImageLauncher.launch(imagePickerIntent)
    }

    private fun setupDatePicker(view: View, date: Calendar, anotherDatePicker: Boolean = false, anotherDate: Calendar? = null) {
        val year = date.get(Calendar.YEAR)
        val month = date.get(Calendar.MONTH)
        val day = date.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            view.context,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                date.set(year, monthOfYear, dayOfMonth)
                if (anotherDatePicker) {
                    setupDatePicker(view, anotherDate!!)
                } else {
                    datesTextView.text = "${dateUtils.formatDate(startDate.timeInMillis)} - ${dateUtils.formatDate(endDate.timeInMillis)}"
                }
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun onUploadApartmentButtonClicked(view: View) {
        val isValidTitle = RequiredValidation.validateRequiredTextField(titleTextField, "title")
        val isValidDescription = RequiredValidation.validateRequiredTextField(descriptionTextField, "description")
        val isValidRooms = RequiredValidation.validateRequiredTextField(roomsTextField, "rooms")
        val isValidPrice = RequiredValidation.validateRequiredTextField(priceTextField, "price")

        if (isValidTitle && isValidDescription && isValidRooms && isValidPrice) { // TODO validate all...
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    Log.d(TAG, "firebase create apartment document")
                    val title = titleTextField.text.toString()
                    val userId = AuthModel.instance.getUserId()!!
                    val description = descriptionTextField.text.toString()
                    val numOfRooms = roomsTextField.text.toString().toInt()
                    val price = priceTextField.text.toString().toInt()
                    val type = typeSelectField.selectedItem.toString()
                    val location = locationSelectField.selectedItem.toString()
                    val imageUrl = FirebaseStorageModel.instance.addImageToFirebaseStorage(imageUri!!, FirebaseStorageModel.APARTMENTS_PATH)

                    val apartment = Apartment("", userId, title, price, description, location, ApartmentType.valueOf(type), numOfRooms, startDate.timeInMillis, endDate.timeInMillis, imageUrl)

                    ApartmentModel.instance.addApartment(apartment)
                    withContext(Dispatchers.Main) {
                        Navigation.findNavController(view).popBackStack(R.id.apartmentsFragment, false)
                    }
                } catch (e: Exception) {
                    Log.e(RegisterFragment.TAG, "An unexpected error occurred: ${e.message}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            MyApplication.Globals.appContext,
                            "failed to upload apartment",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
            }
        }
    }
}