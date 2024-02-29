package com.example.apartments.model.user

import android.os.Looper
import android.util.Log
import androidx.core.os.HandlerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.apartments.common.FireStoreModel
import com.example.apartments.dao.AppLocalDatabase
import com.example.apartments.model.user.User
import com.google.firebase.Timestamp
import kotlinx.coroutines.tasks.await
import java.util.concurrent.Executors

class UserModel private constructor() {
    enum class LoadingState {
        LOADING,
        LOADED
    }

    private val roomDB = AppLocalDatabase.db
    private val firebaseDB = FireStoreModel.instance.db
    private var executor = Executors.newSingleThreadExecutor()
    private var mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())
    val usersLoadingState: MutableLiveData<UserModel.LoadingState> = MutableLiveData(UserModel.LoadingState.LOADED)

    companion object {
        const val USERS_COLLECTION_PATH = "users"
        val instance: UserModel = UserModel()
    }

//    fun getAllApartments(): LiveData<MutableList<User>> {
//        refreshAllApartments()
//       // return roomDB.apartmentDao().getAll()
//    }

    private fun getAllUsersFromFirestore(since: Long, callback: (List<User>) -> Unit) {
        firebaseDB.collection(USERS_COLLECTION_PATH)
            .whereGreaterThanOrEqualTo(User.LAST_UPDATED, Timestamp(since, 0))
            .get().addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val users : MutableList<User> = mutableListOf()
                        for (json in it.result) {
                            users.add(User.fromJson(json.data, json.id))
                        }

                        callback(users)
                    }

                    false -> callback(listOf())
                }
            }
    }


//    fun refreshAllApartments() {
//        apartmentsListLoadingState.value = LoadingState.LOADING
//
//        // 1. Get the latest local update date
//        val lastUpdated: Long = Apartment.lastUpdated
//
//
//        // 2.Fetch all the updates from the cloud that was made later than the local last update date
//        getAllApartmentsFromFirestore(lastUpdated) { apartments ->
//            Log.i("TAG", "Firebase returned ${apartments.size}, lastUpdated: $lastUpdated")
//            // 3. Update the local records
//            executor.execute {
//                var time = lastUpdated
//                for (apartment in apartments) {
//                    roomDB.apartmentDao().insert(apartment)
//
//                    apartment.lastUpdated?.let {
//                        if (time < it)
//                            time = apartment.lastUpdated ?: System.currentTimeMillis()
//                    }
//                }
//
//                // 4. Update the local last update date
//                Apartment.lastUpdated = time
//                apartmentsListLoadingState.postValue(LoadingState.LOADED)
//            }
//        }
//    }

//    fun addApartment(apartment: Apartment, callback: () -> Unit) {
//        firebaseDB.collection(APARTMENTS_COLLECTION_PATH).add(apartment.json).addOnSuccessListener {
//            refreshAllApartments()
//            callback()
//        }
//    }

    suspend fun addUser(user: User) {
        firebaseDB.collection(USERS_COLLECTION_PATH).document(user.id).set(user.json).await()
    }
}