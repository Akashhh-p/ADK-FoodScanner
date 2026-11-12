package com.example.foodscanner.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class FirebaseAuthManager {

    private val firebaseAuth = FirebaseAuth.getInstance()

    suspend fun register(name: String, email: String, password: String): Result<String> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user

            if (user != null) {
                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                user.updateProfile(profileUpdate).await()
                Result.success(user.uid)
            } else {
                Result.failure(Exception("User registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<String> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user
            if (user != null) {
                Result.success(user.uid)
            } else {
                Result.failure(Exception("Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUser() = firebaseAuth.currentUser

    fun getUserEmail() = firebaseAuth.currentUser?.email ?: ""

    fun getUserName() = firebaseAuth.currentUser?.displayName ?: ""

    fun getUserId() = firebaseAuth.currentUser?.uid ?: ""

    fun isLoggedIn() = firebaseAuth.currentUser != null

    fun logout() {
        firebaseAuth.signOut()
    }
}
