package com.example.azp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private var _userLiveData = MutableLiveData<FirebaseUser?>()
    val userLiveData: LiveData<FirebaseUser?> = _userLiveData

    val emailLiveData = MutableLiveData<String?>()

    fun signInWithEmailAndPassword(email: String, password: String) {
        _userLiveData = authRepository.signInWithEmailAndPassword(email, password) as MutableLiveData<FirebaseUser?>
        _userLiveData.observeForever { user ->
            if (user != null) {
                emailLiveData.value = user.email
            } else {
                emailLiveData.value = null
            }
        }
    }

    fun createUserWithEmailAndPassword(email: String, password: String) {
        _userLiveData = authRepository.createUserWithEmailAndPassword(email, password) as MutableLiveData<FirebaseUser?>
        _userLiveData.observeForever { user ->
            if (user != null) {
                emailLiveData.value = user.email
            } else {
                emailLiveData.value = null
            }
        }
    }

    fun getCurrentUser() {
        _userLiveData.value = authRepository.getCurrentUser()
        emailLiveData.value = _userLiveData.value?.email
    }

    fun signOut() {
        authRepository.signOut()
        emailLiveData.value = null
    }

    constructor() : this(AuthRepository(FirebaseAuth.getInstance()))
}
