package com.example.azp.data_classes

import android.icu.text.CaseMap.Title
import android.net.Uri


enum class TaskState {
    TODO,
    IN_PROGRESS,
    COMPLETED,
    MILESTONE,
    NONE
}

class Task(
    private var id: String = "",
    private var title: String = "",
    private var description: String = "",
    private var state: TaskState = TaskState.NONE,
    private var dueDate: Date = Date.now(),
    private var dateCompleted: Date = Date(1,1,1)

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

    fun setTitle(newTitle: String){
        title = newTitle
    }

    fun setDescription(newDescription: String){
        description = newDescription
    }

    fun setDate(newDate: Date){
        dueDate = newDate
    }

    fun  setTaskState(newState: TaskState){
        state = newState
    }

    fun setDateCom(newDate: Date) {
        dateCompleted = newDate
    }

    fun getDateCom() :Date{
        return dateCompleted
    }

}