package com.example.azp.utilities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseUser

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _userLiveData = MutableLiveData<FirebaseUser?>()

    fun signIn(email: String, password: String) {
        authRepository.signInWithEmailAndPassword(email, password)
    }

    fun createUser(email: String, password: String) {
        authRepository.createUserWithEmailAndPassword(email, password)
    }

    fun getCurrentUser():LiveData<FirebaseUser?> {
        _userLiveData.value = authRepository.getCurrentUser()
        return _userLiveData
    }

    fun guestUser(){
        authRepository.guestUser()
    }

    fun checkUser():Boolean{
        return authRepository.checkUser()
    }

    fun signOut() {
        authRepository.signOut()
        _userLiveData.value = null
    }
    fun fromGuestToUser(email: String,password: String) {
        authRepository.fromGuestToUser(email,password)
    }
}

class AuthViewModelFactory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown class ViewModel")
    }
}