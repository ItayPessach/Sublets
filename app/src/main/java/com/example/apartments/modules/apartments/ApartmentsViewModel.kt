package com.example.apartments.modules.apartments

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apartments.model.apartment.Apartment
import com.example.apartments.model.apartment.ApartmentModel
import com.example.apartments.model.user.UserModel
import kotlinx.coroutines.launch

class ApartmentsViewModel: ViewModel() {
    var apartments: LiveData<MutableList<Apartment>>? = null

    fun onLikeClick(apartmentId: String, liked: Boolean) {
        viewModelScope.launch {
            if (liked) {
                UserModel.instance.addLikedApartment(apartmentId)
            } else {
                UserModel.instance.removeLikedApartment(apartmentId)
            }
        }
    }

    fun getAllApartments(callback: () -> Unit) {
        viewModelScope.launch {
            apartments = ApartmentModel.instance.getAllApartments()
            callback()
        }
    }

    fun getAllLikedApartments(): LiveData<MutableList<Apartment>> {
        return ApartmentModel.instance.getAllLikedApartments()
    }

    fun getAllMyApartments(): LiveData<MutableList<Apartment>> {
        return ApartmentModel.instance.getAllMyApartments()
    }

    fun refreshAllApartments(callback: () -> Unit) {
        viewModelScope.launch {
            ApartmentModel.instance.refreshAllApartments()
            callback()
        }
    }
}