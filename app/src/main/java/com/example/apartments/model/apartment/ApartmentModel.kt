package com.example.apartments.model.apartment

import android.os.Looper
import android.util.Log
import androidx.core.os.HandlerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.apartments.common.FirebaseModel
import com.example.apartments.dao.AppLocalDatabase
import com.google.firebase.Timestamp
import java.util.concurrent.Executors

class ApartmentModel private constructor() {
    enum class LoadingState {
        LOADING,
        LOADED
    }

    private val roomDB = AppLocalDatabase.db
    private val firebaseDB = FirebaseModel.instance.db
    private var executor = Executors.newSingleThreadExecutor()
    private var mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())
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

    fun addApartment(apartment: Apartment, callback: () -> Unit) {
        firebaseDB.collection(APARTMENTS_COLLECTION_PATH).add(apartment.json).addOnSuccessListener {
            refreshAllApartments()
            callback()
        }
    }
}