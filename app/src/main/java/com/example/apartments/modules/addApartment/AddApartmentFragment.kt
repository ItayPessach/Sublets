package com.example.apartments.modules.addApartment

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.Navigation
import com.example.apartments.R
import com.example.apartments.common.RequiredValidation
import com.example.apartments.databinding.FragmentAddApartmentBinding
import com.example.apartments.model.apartment.Apartment
import com.example.apartments.model.apartment.ApartmentModel
import com.example.apartments.model.apartment.ApartmentType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class AddApartmentFragment : Fragment() {
    private var TAG = "AddApartmentFragment"
    private val locations = arrayOf("Tel Aviv - Yafo", "Haifa") // TODO get from external source
    private val types = arrayOf("Apartment", "House", "Villa", "Penthouse")
    private var startDate: Calendar = Calendar.getInstance()
    private var endDate: Calendar = Calendar.getInstance()
    private var apartmentUri: Any? = null

    private lateinit var titleWidget: EditText
    private lateinit var descriptionWidget: EditText
    private lateinit var roomsWidget: EditText
    private lateinit var priceWidget: EditText
    private lateinit var locationWidget: Spinner
    private lateinit var typeWidget: Spinner
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
            apartmentUri = selectedImageUri
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
        titleWidget = binding.etUploadApartmentTitle
        descriptionWidget = binding.etUploadApartmentDescription
        roomsWidget = binding.etUploadApartmentRooms
        priceWidget = binding.etUploadApartmentPrice
        locationWidget = binding.spUploadApartmentLocation
        typeWidget = binding.spUploadApartmentType
        datesTextView = binding.tvUploadApartmentDates
        datesBtn = binding.ibUploadApartmentDates
        addImageBtn = binding.ibUploadApartmentAddPhotoButton
        uploadApartmentBtn = binding.btnUploadApartmentUpload

        val locationAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, locations)
        locationAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        locationWidget.adapter = locationAdapter

        val typeAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, types)
        typeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        typeWidget.adapter = typeAdapter

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
                    datesTextView.text = "${formatDate(startDate.timeInMillis)} - ${formatDate(endDate.timeInMillis)}"
                }
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun onUploadApartmentButtonClicked(view: View) {
        val isValidTitle = RequiredValidation.validateRequiredTextField(titleWidget, "title")
        val isValidDescription = RequiredValidation.validateRequiredTextField(descriptionWidget, "description")
        val isValidRooms = RequiredValidation.validateRequiredTextField(roomsWidget, "rooms")
        val isValidPrice = RequiredValidation.validateRequiredTextField(priceWidget, "price")

        if (isValidTitle && isValidDescription && isValidRooms && isValidPrice) { // TODO add location, type and dates validation and actually validate all...
            Log.d(TAG, "firebase create apartment document")
            val title = titleWidget.text.toString()
            val description = descriptionWidget.text.toString()
            val numOfRooms = roomsWidget.text.toString().toInt()
            val price = priceWidget.text.toString().toInt()
            val type = typeWidget.selectedItem.toString()
            val location = locationWidget.selectedItem.toString()
            val dates = datesTextView.text.toString() // get the timestamps
//            val apartmentUrl = FirebaseStorageModel.instance.addImageToFirebaseStorage(apartmentUri!!, FirebaseStorageModel.APARTMENTS_PATH) // todo add

            val apartment = Apartment("", title, description, location, ApartmentType.valueOf(type), numOfRooms) // TODO should also insert price, userId, dates and image uri

            ApartmentModel.instance.addApartment(apartment) {
                Navigation.findNavController(view).popBackStack(R.id.apartmentsFragment, false)
            }
        }
    }

    private fun formatDate(milliseconds: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        return formatter.format(Date(milliseconds))
    }
}