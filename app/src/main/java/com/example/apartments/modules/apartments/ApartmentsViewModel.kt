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

    fun setAllApartments() {
        val apartmentsFromRoomDB = ApartmentModel.instance.getAllApartments()
        val currentUser = UserModel.instance.currentUser!!

        for (apartment in apartmentsFromRoomDB.value ?: mutableListOf()) {
            if (currentUser.likedApartments.contains(apartment.id)) {
                apartment.liked = true
            }

            if (currentUser.id == apartment.userId) {
                apartment.isMine = true
            }
        }

        apartments = apartmentsFromRoomDB
    }

    fun getLikedApartments(): MutableList<Apartment> {
        val likedApartmentsList = mutableListOf<Apartment>()
        val currentUser = UserModel.instance.currentUser!!

        for (apartment in apartments?.value ?: mutableListOf()) {
            if (currentUser.likedApartments.contains(apartment.id)) {
                apartment.liked = true
                likedApartmentsList.add(apartment)
            }
        }

        return likedApartmentsList
    }

    fun getMyApartments(): MutableList<Apartment> {
        val myApartmentsList = mutableListOf<Apartment>()
        val currentUser = UserModel.instance.currentUser!!

        for (apartment in apartments?.value ?: mutableListOf()) {
            if (currentUser.id == apartment.userId) {
                apartment.isMine = true
                myApartmentsList.add(apartment)
            }
        }

        return myApartmentsList
    }

    fun refreshAllApartments() {
        ApartmentModel.instance.refreshAllApartments()
    }
}