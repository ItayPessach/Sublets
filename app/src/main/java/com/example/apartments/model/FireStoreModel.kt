package com.example.apartments.model

import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.ktx.Firebase

class FireStoreModel {
    val db = Firebase.firestore
    companion object {
        val instance: FireStoreModel = FireStoreModel()
    }

    init {
        val settings = firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings {  })
        }

        db.firestoreSettings = settings
    }
}