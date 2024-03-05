package com.example.apartments.modules.expandedApartment

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.example.apartments.R
import com.example.apartments.databinding.FragmentExpandedApartmentBinding
import com.example.apartments.model.apartment.Apartment
import com.example.apartments.model.apartment.ApartmentModel
import com.example.apartments.model.user.UserModel
import com.example.apartments.utils.dateUtils
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import kotlinx.coroutines.launch

class ExpandedApartmentFragment : Fragment() {
    private var TAG = "ExpandedApartmentFragment"

    private lateinit var binding: FragmentExpandedApartmentBinding
    private lateinit var progressBar: ProgressBar
    private lateinit var layout: View
    private lateinit var image: ImageView
    private lateinit var titleTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var locationTextView: TextView
    private lateinit var roomsTextView: TextView
    private lateinit var propertyTypeTextView: TextView
    private lateinit var datesTextView: TextView
    private lateinit var personTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var phoneTextView: TextView

    private val args: ExpandedApartmentFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExpandedApartmentBinding.inflate(inflater, container, false)

        progressBar = binding.pbExpandedApartment
        layout = binding.clExpandedApartmentLayout
        titleTextView = binding.tvExpandedApartmentTitle
        priceTextView = binding.tvExpandedApartmentPrice
        locationTextView = binding.tvExpandedApartmentLocation
        roomsTextView = binding.tvExpandedApartmentRooms
        propertyTypeTextView = binding.tvExpandedApartmentType
        datesTextView = binding.tvExpandedApartmentDates
        personTextView = binding.tvExpandedApartmentPerson
        emailTextView = binding.tvExpandedApartmentEmail
        phoneTextView = binding.tvExpandedApartmentPhone
        image = binding.ivExpandedApartmentImage

        val apartmentId: String = args.apartmentId
        lifecycleScope.launch {
            val apartment = ApartmentModel.instance.getApartmentById(apartmentId)
            if (apartment.value != null)
                bind(apartment.value!!)
            else {
                Log.e(TAG, "Apartment with id $apartmentId not found")
                findNavController().popBackStack()
            }
        }

        return binding.root
    }

    private suspend fun bind(apartment: Apartment) {
        titleTextView.text = apartment.title
        priceTextView.text = "${apartment.pricePerNight.toString()}$"
        locationTextView.text = apartment.city
        roomsTextView.text = apartment.numOfRooms.toString()
        propertyTypeTextView.text = apartment.type.toString()
        datesTextView.text = "${dateUtils.formatDate(apartment.startDate)} - ${dateUtils.formatDate(apartment.endDate)}"

        val user = UserModel.instance.fetchUser(apartment.userId)
        user?.let {
            personTextView.text = user.name
            emailTextView.text = user.email
            phoneTextView.text = user.phoneNumber
        }

        Picasso.get()
            .load(apartment.imageUrl)
            .into(object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    image.setImageBitmap(bitmap)
                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    Log.e(TAG, e.toString())
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    Log.d(TAG, "onPrepareLoad")
                }
            })

        progressBar.visibility = View.GONE
        layout.visibility = View.VISIBLE
    }
}