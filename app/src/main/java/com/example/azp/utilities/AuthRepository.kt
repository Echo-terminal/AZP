package com.example.azp.utilities

import android.util.Log
import com.google.firebase.auth.FirebaseUser

class AuthRepository() {

    fun signInWithEmailAndPassword(email: String, password: String){
        AUTH.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val dateMap = mutableMapOf<String, Any>()
                dateMap[CHILD_UID] = authResult.user?.uid.toString()
                dateMap[CHILD_EMAIL] = authResult.user?.email.toString()
                dateMap[CHILD_USERNAME] = authResult.user?.email.toString()
                REF_DATABASE_ROOT.child(NODE_USER).child(authResult.user?.uid.toString()).updateChildren(dateMap)
            }
            .addOnFailureListener { exception ->
                Log.e("AuthRepository", "signInWithEmailAndPassword: ${exception.message}")
            }
    }

    fun createUserWithEmailAndPassword(email: String, password: String){
        AUTH.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val dateMap = mutableMapOf<String, Any>()
                dateMap[CHILD_UID] = authResult.user?.uid.toString()
                dateMap[CHILD_EMAIL] = authResult.user?.email.toString()
                dateMap[CHILD_USERNAME] = authResult.user?.email.toString()
                REF_DATABASE_ROOT.child(NODE_USER).child(authResult.user?.uid.toString()).updateChildren(dateMap)
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
