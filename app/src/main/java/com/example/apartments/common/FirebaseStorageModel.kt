package com.example.apartments.common

import android.net.Uri
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.util.UUID

class FirebaseStorageModel {
    private val storage = Firebase.storage
    companion object {
        val instance: FirebaseStorageModel = FirebaseStorageModel()
        const val USERS_PATH = "users"
        const val APARTMENTS_PATH = "apartments"
    }

    suspend fun addImageToFirebaseStorage(uri: Uri, path: String): String {
        return try {
            val filename = UUID.randomUUID().toString()

            val imagesRef = storage.reference.child("$path/$filename")
            val uploadTask = imagesRef.putFile(uri).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await()

            downloadUrl.toString()
        } catch (e: Exception) {
            Log.e("TAG", "An unexpected error occurred: ${e.message}")
            ""
        }
    }
}