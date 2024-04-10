package com.example.azp.utilities

import com.example.azp.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

interface TaskFirebaseRepositoryCallback<T> {
    fun onSuccess(result: List<Task>)
    fun onError(e: Exception)
}


class TaskFirebaseRepository() :
    TaskFirebaseRepositoryCallback<Task> {

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference()
    fun add(item: Task, callback: TaskFirebaseRepositoryCallback<Task>) {
        val key = databaseReference.push().key ?: return
        databaseReference.child(key).setValue(item)
            .addOnSuccessListener {
                callback.onSuccess(listOf(item))
            }
            .addOnFailureListener {
                callback.onError(it)
            }
    }

    fun getAll(callback: TaskFirebaseRepositoryCallback<Task>) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

            }

            override fun onCancelled(error: DatabaseError) {
                callback.onError(error.toException())
            }
        })
    }

    fun get(id: String, callback: TaskFirebaseRepositoryCallback<Task>) {
        databaseReference.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                } else {
                    callback.onSuccess(emptyList())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onError(error.toException())
            }
        })
    }

    fun update(item: Task, callback: TaskFirebaseRepositoryCallback<Task>) {
        val id = item.getId() ?: return
        databaseReference.child(id).setValue(item)
            .addOnSuccessListener {
                callback.onSuccess(listOf(item))
            }
            .addOnFailureListener {
                callback.onError(it)
            }
    }

    fun delete(id: String, callback: TaskFirebaseRepositoryCallback<Task>) {
        databaseReference.child(id).removeValue()
            .addOnSuccessListener {
                callback.onSuccess(emptyList()) // Assuming no data to return after deletion
            }
            .addOnFailureListener {
                callback.onError(it)
            }
    }

    override fun onSuccess(result: List<Task>) {

    }

    override fun onError(e: Exception) {

    }

}