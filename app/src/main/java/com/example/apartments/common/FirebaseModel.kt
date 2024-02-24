package com.example.apartments.common

import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.ktx.Firebase

class FirebaseModel {
    val db = Firebase.firestore
    companion object {
        val instance: FirebaseModel = FirebaseModel()
    }

    init {
        val settings = firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings {  })
        }

        db.firestoreSettings = settings
    }
}