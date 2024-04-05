package com.example.azp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TaskViewModel(private val firebaseRepository: FirebaseRepository) : ViewModel() {

    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> = _tasks

    init {
        getAllTasks() // Load tasks initially
    }

    fun getAllTasks() {
        firebaseRepository.getAll(object : FirebaseRepositoryCallback<Task> {
            override fun onSuccess(result: List<Task>) {
                _tasks.value = result
            }

            override fun onError(e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        })
    }

    fun addTask(task: Task) {
        firebaseRepository.add(task, object : FirebaseRepositoryCallback<Task> {
            override fun onSuccess(result: List<Task>) {
                getAllTasks()
            }

            override fun onError(e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        })
    }

    fun updateTask(task: Task) {
        firebaseRepository.update(task, object : FirebaseRepositoryCallback<Task> {
            override fun onSuccess(result: List<Task>) {
                getAllTasks() // Refresh task list after updating
            }

            override fun onError(e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        })
    }

    fun deleteTask(id: String) {
        firebaseRepository.delete(id, object : FirebaseRepositoryCallback<Task> {
            override fun onSuccess(result: List<Task>) {
                getAllTasks()
            }

            override fun onError(e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        })
    }
}
