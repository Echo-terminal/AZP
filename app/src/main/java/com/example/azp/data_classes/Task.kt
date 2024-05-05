package com.example.azp.data_classes


enum class TaskState {
    TODO,
    IN_PROGRESS,
    COMPLETED,
    MILESTONE
}

class Task(
    private var id: String = "",
    private var title: String = "",
    private var description: String = "",
    private var state: TaskState = TaskState.TODO,
    private var dueDate: Date = Date.now()
) {

    fun getId(): String{
        return id
    }
    fun getTitle(): String {
        return title
    }

    fun getDescription(): String {
        return description
    }

    fun getState(): TaskState {
        return state
    }

    fun getDueDate(): Date {
        return dueDate
    }

    fun setId(newId: String){
        id = newId
    }
}