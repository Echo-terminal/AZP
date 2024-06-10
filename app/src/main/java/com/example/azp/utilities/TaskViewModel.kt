package com.example.azp.utilities

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.azp.data_classes.Date
import com.example.azp.data_classes.Task
import com.example.azp.data_classes.TaskState
import com.example.azp.data_classes.User

class TaskViewModel(private val firebaseRepository: TaskFirebaseRepository) : ViewModel() {

    private val _tasks = MutableLiveData<List<Task>>()

    init {
        getAllTasks() // Load tasks initially
    }

    fun getAllTasks(): LiveData<List<Task>> {
        firebaseRepository.getAllTasks(object : TaskFirebaseRepositoryCallback<Task> {
            override fun onSuccess(result: List<Task>) {
                _tasks.value = result
            }

            override fun onError(e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        })
        return _tasks
    }

    fun getTaskType(callback: (List<Int>) -> Unit) {
        val listLiveData = mutableListOf<Int>()
        firebaseRepository.getTaskTypes { task ->
            listLiveData.add(task[0])
            listLiveData.add(task[1])
            listLiveData.add(task[2])
            listLiveData.add(task[3])
            callback(listLiveData)
        }
    }

    fun getAllSortedTasksOfMonth(month: Int, callback: (List<Task>) -> Unit) {
        firebaseRepository.getAllTasks(object : TaskFirebaseRepositoryCallback<Task> {
            override fun onSuccess(result: List<Task>) {
                val sortedTasks = result.filter { it.getDueDate().getMonth() == month }.sortedBy { it.getDueDate().toString() }
                callback(sortedTasks)
            }

            override fun onError(e: Exception) {
                e.printStackTrace()
            }
        })
    }

    fun getAllCompletedTasks(list: List<Task>, callback: (List<Task>) -> Unit){
        val completedTasks = mutableListOf<Task>()
        for (task in list) {
            when (task!!.getState()) {
                TaskState.NONE, TaskState.TODO, TaskState.IN_PROGRESS, TaskState.MILESTONE -> continue
                TaskState.COMPLETED -> completedTasks.add(task)
            }
        }
        callback(completedTasks)
    }



    fun getCompletedTasksInRange(
        startDate: Date,
        endDate: Date,
        callback: (List<Task>) -> Unit
    ) {
        firebaseRepository.getAllTasks(object : TaskFirebaseRepositoryCallback<Task> {
            override fun onSuccess(result: List<Task>) {
                val tasksForDate = mutableListOf<Task>()
                for (task in result) {
                    if (task.getState() == TaskState.COMPLETED) {
                        val completionDate = task.getDateCom()
                        if (completionDate != null && completionDate.isAfter(startDate) && completionDate.isBefore(endDate)) {
                            tasksForDate.add(task)
                        }
                    }
                }
                callback(tasksForDate)
            }

            override fun onError(e: Exception) {
                e.printStackTrace()
            }
        })
    }



    fun getAllTasksByDate(date: Date, callback: (List<Task>) -> Unit) {
        firebaseRepository.getAllTasks(object : TaskFirebaseRepositoryCallback<Task> {
            override fun onSuccess(result: List<Task>) {
                val tasksForDate = mutableListOf<Task>()
                for (task in result) {
                    if (task.getDueDate() == date) {
                        tasksForDate.add(task)
                    }
                }
                callback(tasksForDate)
            }

            override fun onError(e: Exception) {
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
                //getAllTasks() // Refresh task list after updating
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

    private val _currentUser = MutableLiveData<User>()

    fun getCurrentUser(): LiveData<User> {
        firebaseRepository.getCurrentUser(object : UserFirebaseRepositoryCallback<User> {
            override fun onSuccess(result: User) {
                _currentUser.value = result
            }

            override fun onError(e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        })
        return _currentUser
    }

    fun updateUser(user: User) {
        firebaseRepository.updateUser(user, object : UserFirebaseRepositoryCallback<User> {
            override fun onSuccess(result: User) {
                _currentUser.value = result
            }

            override fun onError(e: Exception) {
                e.printStackTrace()
            }
        })
    }

    fun getCompletedTask(completion: CompletionPercentageCallback) {
        firebaseRepository.getCompletedTask(completion)
    }
}

class TaskViewModelFactory(private val firebaseRepository: TaskFirebaseRepository ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(firebaseRepository) as T
        }
        throw IllegalArgumentException("Unknown class ViewModel")
    }
}