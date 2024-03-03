package com.example.apartments.model.apartment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.apartments.common.FireStoreModel
import com.example.apartments.dao.AppLocalDatabase
import com.example.apartments.model.auth.AuthModel
import com.example.apartments.model.user.UserModel
import com.google.firebase.Timestamp
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ApartmentModel private constructor() {
    enum class LoadingState {
        LOADING,
        LOADED
    }

    private val roomDB = AppLocalDatabase.db
    private val firebaseDB = FireStoreModel.instance.db
    private var executor = Executors.newSingleThreadExecutor()
    val apartmentsListLoadingState: MutableLiveData<LoadingState> = MutableLiveData(LoadingState.LOADED)
    val likedApartmentsListLoadingState: MutableLiveData<LoadingState> = MutableLiveData(LoadingState.LOADED)

    companion object {
        const val APARTMENTS_COLLECTION_PATH = "apartments"
        val instance: ApartmentModel = ApartmentModel()
    }

    suspend fun getAllApartments(): LiveData<MutableList<Apartment>> {
        refreshAllApartments()
        return roomDB.apartmentDao().getAll()
    }

    fun getAllLikedApartments(): LiveData<MutableList<Apartment>> {
        return roomDB.apartmentDao().getAllLikedApartments()
    }

    fun getAllMyApartments(): LiveData<MutableList<Apartment>> {
        return roomDB.apartmentDao().getAllMyApartments()
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

   suspend fun refreshAllApartments() {
       apartmentsListLoadingState.value = LoadingState.LOADING
       val loggedInUser = UserModel.instance.fetchUser(AuthModel.instance.getUserId()!!)

       // 1. Get the latest local update date
       val lastUpdated: Long = Apartment.lastUpdated


       // 2.Fetch all the updates from the cloud that was made later than the local last update date
       getAllApartmentsFromFirestore(lastUpdated) { apartments ->
           Log.i("TAG", "Firebase returned ${apartments.size}, lastUpdated: $lastUpdated")
           // 3. Update the local records
           executor.execute {
               var time = lastUpdated
               for (apartment in apartments) {
                   apartment.liked = loggedInUser!!.likedApartments.contains(apartment.id)
                   apartment.isMine = loggedInUser.id == apartment.userId
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
        suspendCoroutine<Unit> { continuation ->
            firebaseDB.collection(APARTMENTS_COLLECTION_PATH).add(apartment.json).addOnSuccessListener {
                continuation.resume(Unit)
            }.addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
        }

        refreshAllApartments()
    }

}