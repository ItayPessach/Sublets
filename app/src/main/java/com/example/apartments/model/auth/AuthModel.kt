package com.example.apartments.model.auth

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AuthModel {
    private val auth = Firebase.auth
    companion object {
        val instance: AuthModel = AuthModel()
    }

    suspend fun signUp(email: String, password: String): AuthResult {
        return auth.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun signIn(email: String, password: String): AuthResult {
       return auth.signInWithEmailAndPassword(email, password).await()
    }

    fun signOut() {
        auth.signOut()
    }

    fun getUser() = auth.currentUser
}