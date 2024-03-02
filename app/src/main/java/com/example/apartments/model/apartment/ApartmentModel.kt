package com.example.apartments.model.apartment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.apartments.common.FireStoreModel
import com.example.apartments.dao.AppLocalDatabase
import com.google.firebase.Timestamp
import java.util.concurrent.Executors

class ApartmentModel private constructor() {
    enum class LoadingState {
        LOADING,
        LOADED
    }

    private val roomDB = AppLocalDatabase.db
    private val firebaseDB = FireStoreModel.instance.db
    private var executor = Executors.newSingleThreadExecutor()
    val apartmentsListLoadingState: MutableLiveData<LoadingState> = MutableLiveData(LoadingState.LOADED)

    companion object {
        const val APARTMENTS_COLLECTION_PATH = "apartments"
        val instance: ApartmentModel = ApartmentModel()
    }

    fun getAllApartments(): LiveData<MutableList<Apartment>> {
        refreshAllApartments()
        return roomDB.apartmentDao().getAll()
    }

    private fun getAllApartmentsFromFirestore(since: Long, callback: (List<Apartment>) -> Unit) {
        firebaseDB.collection(APARTMENTS_COLLECTION_PATH)
            .whereGreaterThanOrEqualTo(Apartment.LAST_UPDATED, Timestamp(since, 0))
            .get().addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val apartments : MutableList<Apartment> = mutableListOf()
                        for (json in it.result) {
                            apartments.add(Apartment.fromJson(json.data, json.id))
                        }

                        callback(apartments)
                    }

                    false -> callback(listOf())
                }
            }
    }


   fun refreshAllApartments() {
       apartmentsListLoadingState.value = LoadingState.LOADING

       // 1. Get the latest local update date
       val lastUpdated: Long = Apartment.lastUpdated


       // 2.Fetch all the updates from the cloud that was made later than the local last update date
       getAllApartmentsFromFirestore(lastUpdated) { apartments ->
           Log.i("TAG", "Firebase returned ${apartments.size}, lastUpdated: $lastUpdated")
           // 3. Update the local records
           executor.execute {
               var time = lastUpdated
               for (apartment in apartments) {
                   // TODO check if the apartment is liked by the user and add liked boolean attribute to the apartment
                   // get the user context, get the user liked sublets and check if the current sublet's id is in it.
                   // if it's in it then set the liked attribute to true
                   apartment.liked = false // TODO set the liked to the correct value
                   // TODO also add if the apartment is the user's apartment and add the attribute isMine to the apartment
                   roomDB.apartmentDao().insert(apartment)

                   apartment.lastUpdated?.let {
                       if (time < it)
                           time = apartment.lastUpdated ?: System.currentTimeMillis()
                   }
               }

               // 4. Update the local last update date
               Apartment.lastUpdated = time
               apartmentsListLoadingState.postValue(LoadingState.LOADED)
           }
       }
   }

    suspend fun addApartment(apartment: Apartment) {
        firebaseDB.collection(APARTMENTS_COLLECTION_PATH).add(apartment.json).addOnSuccessListener {
            refreshAllApartments()
        }
    }

    fun addLikedApartment(apartmentId: String) {
        // addLikedApartmentToFirestore(apartmentId) // TODO add liked apartment to user document in the liked array

        executor.execute {
            roomDB.apartmentDao().setApartmentLiked(apartmentId, true)
        }
    }

    fun removeLikedApartment(apartmentId: String) {
        // removeLikedApartmentFromFirestore(apartmentId) // TODO remove liked apartment from user document in the liked array

        executor.execute {
            roomDB.apartmentDao().setApartmentLiked(apartmentId, false)
        }
    }
}