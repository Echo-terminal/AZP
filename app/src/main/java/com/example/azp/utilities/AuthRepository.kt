package com.example.azp.utilities

import android.R.attr.password
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.azp.data_classes.Task
import com.example.azp.data_classes.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class AuthRepository {

    fun signInWithEmailAndPassword(email: String, password: String){
        val tasksList = MutableLiveData<List<Task>>()
        val taskRepository=TaskFirebaseRepository()
        taskRepository.getAllTasks(object : TaskFirebaseRepositoryCallback<Task> {
            override fun onSuccess(result: List<Task>) {
                tasksList.value = result
            }
            override fun onError(e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        })
        AUTH.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                UID = authResult.user?.uid.toString()
                Log.d("AuthRepository", "signInWithEmailAndPassword: $authResult")
                tasksList.value?.forEach { task ->
                    taskRepository.add(task, object : TaskFirebaseRepositoryCallback<Task>{
                        override fun onSuccess(result: List<Task>) {
                            Log.d("add","succes")
                        }

                        override fun onError(e: Exception) {
                            // Handle error
                            e.printStackTrace()
                        }
                    })
                }
            }
            .addOnFailureListener { exception ->
                Log.e("AuthRepository", "signInWithEmailAndPassword: ${exception.message}")
            }
    }

    fun createUserWithEmailAndPassword(email: String, password: String){
        AUTH.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                UID = authResult.user?.uid.toString()
                val user = User(UID, email, email)
                REF_DATABASE_ROOT.child(NODE_USER).child(UID).setValue(user)
            }
            .addOnFailureListener { exception ->
                Log.e("AuthRepository", "createUserWithEmailAndPassword: ${exception.message}")
            }
    }

    fun guestUser()
    {
        AUTH.signInAnonymously()
            .addOnSuccessListener { authResult->
               Log.d("AuthRepository","LOGIN GUEST" )
                UID = authResult.user?.uid.toString()
                val user = User(UID, "Guest","Guest+${UID}")
                REF_DATABASE_ROOT.child(NODE_USER).child(UID).setValue(user)
            }
            .addOnFailureListener { exception ->
                Log.e("AuthRepository", "guestUser: ${exception.message}")
            }
    }

    fun fromGuestToUser(email: String, password: String){
        val uid = AUTH.uid
        val tasksList = MutableLiveData<List<Task>>()
        val taskRepository = TaskFirebaseRepository()
        taskRepository.getAllTasks(object : TaskFirebaseRepositoryCallback<Task> {
            override fun onSuccess(result: List<Task>) {
                tasksList.value = result
            }
            override fun onError(e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        })
        val credential = EmailAuthProvider.getCredential(email,password)
        AUTH.currentUser?.linkWithCredential(credential)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                signOut()
                signInWithEmailAndPassword(email,password)
                UID = uid.toString()
                val user = User(UID,email,email)
                REF_DATABASE_ROOT.child(NODE_USER).child(UID).setValue(user)
                tasksList.value?.forEach { task ->
                    taskRepository.add(task, object : TaskFirebaseRepositoryCallback<Task>{
                        override fun onSuccess(result: List<Task>) {
                            Log.d("add","succes")
                        }

                        override fun onError(e: Exception) {
                            // Handle error
                            e.printStackTrace()
                        }
                    })
                }
            }
            else {
                task.exception?.printStackTrace()
            }
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return AUTH.currentUser
    }

    fun checkUser(): Boolean {
        return AUTH.currentUser!=null
    }

    fun signOut() {
        AUTH.signOut()
        UID = ""
    }
}
