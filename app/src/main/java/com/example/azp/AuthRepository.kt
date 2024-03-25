package com.example.azp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthRepository(private val firebaseAuth: FirebaseAuth) {

    fun signInWithEmailAndPassword(email: String, password: String): LiveData<FirebaseUser?> {
        val mutableLiveData = MutableLiveData<FirebaseUser?>()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                mutableLiveData.value = authResult.user
            }
            .addOnFailureListener { exception ->
                mutableLiveData.value = null
                Log.e("AuthRepository", "signInWithEmailAndPassword: ${exception.message}")
            }
        return mutableLiveData
    }

    fun createUserWithEmailAndPassword(email: String, password: String): LiveData<FirebaseUser?> {
        val mutableLiveData = MutableLiveData<FirebaseUser?>()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                mutableLiveData.value = authResult.user
            }
            .addOnFailureListener { exception ->
                mutableLiveData.value = null
                Log.e("AuthRepository", "createUserWithEmailAndPassword: ${exception.message}")
            }
        return mutableLiveData
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}
