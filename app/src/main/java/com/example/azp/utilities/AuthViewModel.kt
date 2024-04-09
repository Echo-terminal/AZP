package com.example.azp.utilities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseUser

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _userLiveData = MutableLiveData<FirebaseUser?>()
    val userLiveData: LiveData<FirebaseUser?> = _userLiveData

    val emailLiveData = MutableLiveData<String?>()

    fun signIn(email: String, password: String) {
        authRepository.signInWithEmailAndPassword(email, password)
    }

    fun createUser(email: String, password: String) {
        authRepository.createUserWithEmailAndPassword(email, password)
    }

    fun getCurrentUser() {
        _userLiveData.value = authRepository.getCurrentUser()
        emailLiveData.value = _userLiveData.value?.email
    }

    fun signOut() {
        authRepository.signOut()
        _userLiveData.value = null
        emailLiveData.value = null
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