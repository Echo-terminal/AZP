package com.example.azp


enum class TaskState {
    TODO,
    IN_PROGRESS,
    COMPLETED,
    MILESTONE
}

class Task
    (
    private var id: String,
    private var title: String,
    private var description: String,
    private var state: TaskState = TaskState.TODO,
    private var dueDate: String
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

    fun getDueDate(): String {
        return dueDate
    }

    // Setters
    fun setTitle(newTitle: String) {
        title = newTitle
    }

    fun setDescription(newDescription: String) {
        description = newDescription
    }

    fun setState(newState: TaskState) {
        state = newState
    }

    fun setDueDate(newDueDate: String) {
        dueDate = newDueDate
    }

}