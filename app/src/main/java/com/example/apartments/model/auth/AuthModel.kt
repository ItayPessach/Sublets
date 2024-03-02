package com.example.apartments.model.auth

import android.util.Log
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AuthModel {
    private val auth = Firebase.auth
    companion object {
        val instance: AuthModel = AuthModel()
        const val TAG = "AuthModel"
    }

    suspend fun signUp(email: String, password: String): AuthResult {
        Log.d(TAG, "user sign up with email: $email and password: $password")
        return auth.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun signIn(email: String, password: String): AuthResult {
        Log.d(TAG, "user sign in with email: $email and password: $password")
       return auth.signInWithEmailAndPassword(email, password).await()
    }

    fun signOut() {
        Log.d(TAG, "logout user")
        auth.signOut()
    }

    fun getUser() = auth.currentUser

    fun getUserId() = auth.currentUser?.uid
}