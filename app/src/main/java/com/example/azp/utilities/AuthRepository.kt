package com.example.azp.utilities

import android.util.Log
import com.example.azp.data_classes.User
import com.google.firebase.auth.FirebaseUser

class AuthRepository {

    fun signInWithEmailAndPassword(email: String, password: String){
        AUTH.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val user = User(authResult.user?.uid.toString(), email, email)
                REF_DATABASE_ROOT.child(NODE_USER).child(authResult.user?.uid.toString()).setValue(user)
            }
            .addOnFailureListener { exception ->
                Log.e("AuthRepository", "signInWithEmailAndPassword: ${exception.message}")
            }
    }

    fun createUserWithEmailAndPassword(email: String, password: String){
        AUTH.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val user = User(authResult.user?.uid.toString(), email, email)
                REF_DATABASE_ROOT.child(NODE_USER).child(authResult.user?.uid.toString()).setValue(user)
            }
            .addOnFailureListener { exception ->
                Log.e("AuthRepository", "createUserWithEmailAndPassword: ${exception.message}")
            }
    }

    fun getCurrentUser(): FirebaseUser? {
        return AUTH.currentUser
    }

    fun signOut() {
        AUTH.signOut()
    }
}
