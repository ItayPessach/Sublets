package com.example.apartments.model.user

import android.util.Log
import com.example.apartments.common.FireStoreModel
import com.example.apartments.dao.AppLocalDatabase
import com.example.apartments.model.auth.AuthModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class UserModel private constructor() {
    private val roomDB = AppLocalDatabase.db
    private val firebaseDB = FireStoreModel.instance.db
    var currentUser: User? = null

    companion object {
        const val TAG = "UserModel"
        const val USERS_COLLECTION_PATH = "users"
        val instance: UserModel = UserModel()
    }

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

    suspend fun addUser(user: User) {
        Log.d(TAG, "add user: $user")
        firebaseDB.collection(USERS_COLLECTION_PATH).document(user.id).set(user.json).await()
    }

    suspend fun updateMe(updateUserInput: UpdateUserInput) {
        Log.d(TAG, "update user with data: $updateUserInput")
        firebaseDB.collection(USERS_COLLECTION_PATH).document(AuthModel.instance.getUserId()!!).update(updateUserInput.json)
        getMe()
    }

    suspend fun getMe() {
        Log.d(TAG, "get me")
        currentUser = fetchUser(AuthModel.instance.getUserId()!!)
    }

    suspend fun fetchUser(userId: String): User? {
        Log.d(TAG, "fetch user with id $userId")
        return try {
            val documentSnapshot = firebaseDB.collection(USERS_COLLECTION_PATH).document(userId).get().await()
            return User.fromJson(documentSnapshot.data!!, userId)
         } catch (e: Exception) {
            Log.e("TAG", "An unexpected error occurred: ${e.message}")
            null
         }
    }

    suspend fun addLikedApartment(apartmentId: String) {
        suspendCoroutine { continuation ->
            firebaseDB.collection(USERS_COLLECTION_PATH)
                .document(AuthModel.instance.getUserId()!!)
                .update(User.LIKED_APARTMENTS_KEY, FieldValue.arrayUnion(apartmentId))
                .addOnSuccessListener {
                    currentUser?.likedApartments?.add(apartmentId)
                    continuation.resume(Unit)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

    suspend fun removeLikedApartment(apartmentId: String) {
        suspendCoroutine { continuation ->
            firebaseDB.collection(USERS_COLLECTION_PATH)
                .document(AuthModel.instance.getUserId()!!)
                .update(User.LIKED_APARTMENTS_KEY, FieldValue.arrayRemove(apartmentId))
                .addOnSuccessListener {
                    currentUser?.likedApartments?.remove(apartmentId)
                    continuation.resume(Unit)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }
}