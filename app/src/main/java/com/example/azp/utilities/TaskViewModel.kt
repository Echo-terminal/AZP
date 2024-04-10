package com.example.azp.utilities

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.azp.data_classes.Task

class TaskViewModel(private val firebaseRepository: TaskFirebaseRepository) : ViewModel() {

    private val _tasks = MutableLiveData<List<Task>>()
    init {
        getAllTasks() // Load tasks initially
    }

    fun getAllTasks() {
        firebaseRepository.getAll(object : TaskFirebaseRepositoryCallback<Task> {
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
        firebaseRepository.add(task, object : TaskFirebaseRepositoryCallback<Task> {
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
        firebaseRepository.update(task, object : TaskFirebaseRepositoryCallback<Task> {
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
        firebaseRepository.delete(id, object : TaskFirebaseRepositoryCallback<Task> {
            override fun onSuccess(result: List<Task>) {
                getAllTasks()
            }

            override fun onError(e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        })
    }
}class TaskViewModelFactory(private val firebaseRepository: TaskFirebaseRepository ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(firebaseRepository) as T
        }
        throw IllegalArgumentException("Unknown class ViewModel")
    }
}