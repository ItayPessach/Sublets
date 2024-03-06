package com.example.apartments.modules.expandedApartment

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.apartments.model.apartment.Apartment
import com.example.apartments.model.apartment.ApartmentModel

class ExpandedApartmentViewModel: ViewModel() {
    var apartment: LiveData<Apartment>? = null

    fun setApartment(apartmentId: String) {
        apartment = ApartmentModel.instance.getApartmentById(apartmentId)
    }
}