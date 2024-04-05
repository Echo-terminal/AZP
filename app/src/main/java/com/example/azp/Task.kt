package com.example.azp


enum class TaskState {
    TODO,
    IN_PROGRESS,
    COMPLETED,
    MILESTONE
}

class Task
    (
    private val id: String,
    private var title: String,
    private var description: String,
    private var state: TaskState = TaskState.TODO,
    private var dueDate: Long
) {
    fun getId(): String {
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

    fun getDueDate(): Long {
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

    fun setDueDate(newDueDate: Long) {
        dueDate = newDueDate
    }

}