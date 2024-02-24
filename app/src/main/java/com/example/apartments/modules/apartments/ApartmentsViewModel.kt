package com.example.apartments.modules.apartments

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.apartments.model.apartment.Apartment

class ApartmentsViewModel: ViewModel() {
    var apartments: LiveData<MutableList<Apartment>>? = null
}