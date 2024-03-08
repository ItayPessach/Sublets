package com.example.apartments.modules.upsertApartment.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
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
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.apartments.R
import com.example.apartments.base.MyApplication
import com.example.apartments.common.FirebaseStorageModel
import com.example.apartments.common.RequiredValidation
import com.example.apartments.databinding.FragmentBaseUpsertApartmentBinding
import com.example.apartments.model.apartment.Apartment
import com.example.apartments.model.apartment.ApartmentModel
import com.example.apartments.model.apartment.Type
import com.example.apartments.model.auth.AuthModel
import com.example.apartments.retrofit.RegionsSingelton
import com.example.apartments.utils.dateUtils
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

abstract class BaseUpsertApartmentFragment(val TAG: String) : Fragment() {
    private var startDate: Calendar = Calendar.getInstance()
    private var endDate: Calendar = Calendar.getInstance()
    private var imageUri: Uri? = null
    private val types = arrayOf("Apartment", "House", "Villa", "Penthouse")

    private lateinit var textTextView: TextView
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
    private lateinit var progressBar: ProgressBar
    private lateinit var backButton: ImageButton
    private lateinit var layout: View

    private lateinit var _binding: FragmentBaseUpsertApartmentBinding
    private val binding get() = _binding
    private lateinit var viewModel: BaseUpsertApartmentViewModel

    private val addImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImageUri = result.data?.data

            addImageBtn.setImageURI(selectedImageUri)
            imageUri = selectedImageUri
        }
    }

    protected open fun getApartmentId(): String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBaseUpsertApartmentBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[BaseUpsertApartmentViewModel::class.java]
        progressBar = binding.pbUpsertApartment
        layout = binding.clUpsertApartmentLayout

        val apartmentId = getApartmentId()
        if (apartmentId != null) {
            viewModel.setApartment(apartmentId)

            viewModel.apartment?.observe(viewLifecycleOwner) {
                progressBar.visibility = View.VISIBLE
                layout.visibility = View.GONE
                lifecycleScope.launch {
                    setupUi(it)
                    progressBar.visibility = View.GONE
                    layout.visibility = View.VISIBLE
                }
            }
        } else {
            setupUi()
            progressBar.visibility = View.GONE
            layout.visibility = View.VISIBLE
        }

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    protected fun setupUi(apartment : Apartment? = null) {
        textTextView = binding.tvUpsertApartmentText
        titleTextField = binding.etUpsertApartmentTitle
        descriptionTextField = binding.etUpsertApartmentDescription
        roomsTextField = binding.etUpsertApartmentRooms
        priceTextField = binding.etUpsertApartmentPrice
        locationSelectField = binding.spUpsertApartmentLocation
        typeSelectField = binding.spUpsertApartmentType
        datesTextView = binding.tvUpsertApartmentDates
        datesBtn = binding.ibUpsertApartmentDates
        addImageBtn = binding.ibUpsertApartmentAddPhotoButton
        uploadApartmentBtn = binding.btnUpsertApartmentUpload
        backButton = binding.ibUpsertFragmentBack

        val locationAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, RegionsSingelton.regionsList)
        locationAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        locationSelectField.adapter = locationAdapter

        val typeAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, types)
        typeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        typeSelectField.adapter = typeAdapter

        datesBtn.setOnClickListener(::onDatesButtonClicked)
        addImageBtn.setOnClickListener(::onAddImageButtonClicked)
        backButton.setOnClickListener(::onBackButtonClicked)

        if (apartment != null) {
            Log.d(TAG, "apartment: $apartment")
            textTextView.text = "Edit Your Sublet"
            uploadApartmentBtn.text = "Edit Sublet"
            titleTextField.setText(apartment.title)
            descriptionTextField.setText(apartment.description)
            roomsTextField.setText(apartment.numOfRooms.toString())
            priceTextField.setText(apartment.pricePerNight.toString())
            locationSelectField.setSelection(locationAdapter.getPosition(apartment.city))
            typeSelectField.setSelection(typeAdapter.getPosition(apartment.type.toString()))
            datesTextView.text = "${dateUtils.formatDate(apartment.startDate)} - ${dateUtils.formatDate(apartment.endDate)}"
            imageUri = Uri.parse(apartment.imageUrl)
            startDate.timeInMillis = apartment.startDate
            endDate.timeInMillis = apartment.endDate

            Picasso.get()
                .load(apartment.imageUrl)
                .into(object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        addImageBtn.setImageBitmap(bitmap)
                    }

                    override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                        Log.e(TAG, e.toString())
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                        Log.d(TAG, "onPrepareLoad")
                    }
                })
            uploadApartmentBtn.setOnClickListener { onUpsertApartmentButtonClicked(it, apartment) }
        } else {
            backButton.visibility = View.GONE
            uploadApartmentBtn.setOnClickListener(::onUpsertApartmentButtonClicked)
        }
    }

    private fun onDatesButtonClicked(view: View) {
        setupDatePicker(view, startDate, true, endDate)
    }

    private fun onAddImageButtonClicked(view: View) {
        val imagePickerIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        addImageLauncher.launch(imagePickerIntent)
    }

    @SuppressLint("SetTextI18n")
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

    private fun onUpsertApartmentButtonClicked(view: View, apartment: Apartment? = null) {
        val isValidTitle = RequiredValidation.validateRequiredTextField(titleTextField, "title")
        val isValidDescription = RequiredValidation.validateRequiredTextField(descriptionTextField, "description")
        val isValidRooms = RequiredValidation.validateRequiredTextField(roomsTextField, "rooms")
        val isValidPrice = RequiredValidation.validateRequiredTextField(priceTextField, "price")
        val isValidPhoto = imageUri != null

        if (isValidTitle && isValidDescription && isValidRooms && isValidPrice && isValidPhoto) {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val title = titleTextField.text.toString()
                    val userId = AuthModel.instance.getUserId()!!
                    val description = descriptionTextField.text.toString()
                    val numOfRooms = roomsTextField.text.toString().toInt()
                    val price = priceTextField.text.toString().toInt()
                    val type = typeSelectField.selectedItem.toString()
                    val location = locationSelectField.selectedItem.toString()

                    if (apartment != null) {
                        apartment.title = title
                        apartment.description = description
                        apartment.numOfRooms = numOfRooms
                        apartment.pricePerNight = price
                        apartment.type = Type.valueOf(type)
                        apartment.city = location
                        apartment.startDate = startDate.timeInMillis
                        apartment.endDate = endDate.timeInMillis

                        if (imageUri != Uri.parse(apartment.imageUrl)) {
                            val imageUrl = FirebaseStorageModel.instance.addImageToFirebaseStorage(imageUri!!, FirebaseStorageModel.APARTMENTS_PATH)
                            apartment.imageUrl = imageUrl
                        }

                        ApartmentModel.instance.updateApartment(apartment)
                    } else {
                        val imageUrl = FirebaseStorageModel.instance.addImageToFirebaseStorage(imageUri!!, FirebaseStorageModel.APARTMENTS_PATH)
                        val newApartment = Apartment("", userId, title, price, description, location, Type.valueOf(type), numOfRooms, startDate.timeInMillis, endDate.timeInMillis, imageUrl)
                        ApartmentModel.instance.addApartment(newApartment)
                    }

                    lifecycleScope.launch(Dispatchers.Main) {
                        if (apartment == null) {
                            Toast.makeText(
                                MyApplication.Globals.appContext,
                                "sublet uploaded successfully",
                                Toast.LENGTH_SHORT,
                            ).show()
                            Navigation.findNavController(view).popBackStack(R.id.apartmentsFragment, false)
                        } else {
                            Toast.makeText(
                                MyApplication.Globals.appContext,
                                "sublet updated successfully",
                                Toast.LENGTH_SHORT,
                            ).show()
                            Navigation.findNavController(view).popBackStack()
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "An unexpected error occurred: ${e.message}")
                    Toast.makeText(
                        MyApplication.Globals.appContext,
                        if (apartment == null) "failed to upload sublet" else "failed to update sublet",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        } else {
            Log.e(TAG, "some of the apartment details are missing")
            Toast.makeText(
                MyApplication.Globals.appContext,
                "missing some sublet details",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    private fun onBackButtonClicked(view: View) {
        Navigation.findNavController(view).navigate(R.id.apartmentsFragment)
    }
}