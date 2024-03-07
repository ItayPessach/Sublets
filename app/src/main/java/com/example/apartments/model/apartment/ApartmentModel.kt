package com.example.apartments.model.apartment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.apartments.common.FireStoreModel
import com.example.apartments.dao.AppLocalDatabase
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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
    val apartmentsListLoadingState: MutableLiveData<LoadingState> = MutableLiveData(LoadingState.LOADED)

    companion object {
        const val APARTMENTS_COLLECTION_PATH = "apartments"
        val instance: ApartmentModel = ApartmentModel()
    }

    suspend fun getAllApartments(): LiveData<MutableList<Apartment>> {
        refreshAllApartments()
        return roomDB.apartmentDao().getAll()
    }

    fun getApartment(id: String): LiveData<Apartment> {
        return roomDB.apartmentDao().getApartment(id)
    }

    suspend fun setApartmentLiked(id: String, liked: Boolean) {
        withContext(Dispatchers.IO) {
            roomDB.apartmentDao().setApartmentLiked(id, liked)
        }
    }

    suspend fun deleteApartment(id: String) {
        try {
            firebaseDB.collection(APARTMENTS_COLLECTION_PATH).document(id).delete().await()
            withContext(Dispatchers.IO) {
                roomDB.apartmentDao().deleteApartment(id)
            }
        } catch (exception: Exception) {
            throw exception
        }
    }

    suspend fun refreshAllApartments() {
        withContext(Dispatchers.IO) {
            apartmentsListLoadingState.postValue(LoadingState.LOADING)

            val lastUpdated: Long = Apartment.lastUpdated

            suspendCoroutine { continuation ->
                firebaseDB.collection(APARTMENTS_COLLECTION_PATH)
                    .whereGreaterThanOrEqualTo(Apartment.LAST_UPDATED, Timestamp(lastUpdated, 0))
                    .get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            GlobalScope.launch {
                                var time = lastUpdated
                                for (json in task.result) {
                                    val apartment = Apartment.fromJson(json.data, json.id)
                                    roomDB.apartmentDao().insert(apartment)
                                    apartment.lastUpdated?.let {
                                        if (time < it)
                                            time = apartment.lastUpdated ?: System.currentTimeMillis()
                                    }
                                }
                                Apartment.lastUpdated = time
                                apartmentsListLoadingState.postValue(LoadingState.LOADED)
                                continuation.resume(Unit)
                            }
                        } else {
                            Log.e("TAG", "Error getting documents: ", task.exception)
                            apartmentsListLoadingState.postValue(LoadingState.LOADED)
                            continuation.resumeWithException(task.exception ?: RuntimeException("Unknown error"))
                        }
                    }
            }
        }
    }

    suspend fun addApartment(apartment: Apartment) {
        try {
            firebaseDB.collection(APARTMENTS_COLLECTION_PATH).add(apartment.json).await()
            refreshAllApartments()
        } catch (exception: Exception) {
            throw exception
        }
    }

    suspend fun updateApartment(apartment: Apartment) {
        try {
            firebaseDB.collection(APARTMENTS_COLLECTION_PATH).document(apartment.id).set(apartment.json).await()
            refreshAllApartments()
        } catch (exception: Exception) {
            throw exception
        }
    }
}