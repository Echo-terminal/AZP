package com.example.azp.utilities

import android.util.Log
import com.example.azp.data_classes.Date
import com.example.azp.data_classes.Task
import com.example.azp.data_classes.TaskState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


interface TaskFirebaseRepositoryCallback<T> {
    fun onSuccess(result: List<Task>)
    fun onError(e: Exception)
}


class TaskFirebaseRepository :
    TaskFirebaseRepositoryCallback<Task> {

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference()
    fun add(item: Task, callback: TaskFirebaseRepositoryCallback<Task>) {
        val key = databaseReference.push().key ?: return
        item.setId(key)
        databaseReference.child(NODE_USER).child(UID).child(NODE_TASK).child(key).setValue(item)
            .addOnSuccessListener {
                callback.onSuccess(listOf(item))
            }
            .addOnFailureListener {
                callback.onError(it)
            }
    }
    fun getAllTasks(callback: TaskFirebaseRepositoryCallback<Task>) {

        val tasksRef = databaseReference.child(NODE_USER).child(UID).child(NODE_TASK)

        tasksRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val taskList = mutableListOf<Task>()
                for (taskSnapshot in snapshot.children) {
                    val task = taskSnapshot.getValue(Task::class.java)
                    task?.let {
                        taskList.add(it)
                    }
                }
                callback.onSuccess(taskList)
            }

            override fun onCancelled(error: DatabaseError) {
                callback.onError(error.toException())
            }
        })
    }


    fun getCompletedTask() {
        val completedTasks = mutableListOf<Task>()
        val taskRef = databaseReference.child(NODE_USER).child(UID).child(NODE_TASK)
        taskRef.get().addOnSuccessListener { snapshot ->
            for (taskSnapshot in snapshot.children) {
                val task = taskSnapshot.getValue(Task::class.java)
                when (task!!.getState()) {
                    TaskState.NONE, TaskState.TODO, TaskState.IN_PROGRESS, TaskState.MILESTONE -> continue
                    TaskState.COMPLETED -> completedTasks.add(task)
                    }

                }
            }
    }
    fun getTaskTypes(callback: (List<Int>) -> Unit) {
        val taskTypeList = mutableListOf<Int>().apply {
            add(0)
            add(0)
            add(0)
            add(0)
        }
        val taskRef = databaseReference.child(NODE_USER).child(UID).child(NODE_TASK)
        taskRef.get().addOnSuccessListener { snapshot ->
            for (taskSnapshot in snapshot.children) {
                val task = taskSnapshot.getValue(Task::class.java)
                when (task!!.getState()) {
                    TaskState.NONE -> continue
                    TaskState.TODO -> taskTypeList[0]++
                    TaskState.IN_PROGRESS -> taskTypeList[1]++
                    TaskState.MILESTONE -> taskTypeList[2]++
                    TaskState.COMPLETED -> taskTypeList[3]++
                }
            }
            Log.d("Tasssss", taskTypeList.toString())
            callback(taskTypeList)
        }.addOnFailureListener { exception ->
            Log.e("Firebase", "Ошибка при получении данных: ${exception.message}", exception)
            callback(emptyList())
        }
    }
    fun get(item: Task, callback: TaskFirebaseRepositoryCallback<Task>) {
        val id = item.getId()
        databaseReference.child(NODE_USER).child(UID).child(NODE_TASK).child(id).addListenerForSingleValueEvent(object : ValueEventListener {
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
        val id = item.getId()
        databaseReference.child(NODE_USER).child(UID).child(NODE_TASK).child(id).setValue(item)
            .addOnSuccessListener {
                callback.onSuccess(listOf(item))
            }
            .addOnFailureListener {
                callback.onError(it)
            }
    }

    fun delete(id: String, callback: TaskFirebaseRepositoryCallback<Task>) {
        databaseReference.child(NODE_USER).child(UID).child(NODE_TASK).child(id).removeValue()
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