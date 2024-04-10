package com.example.azp.activities

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.azp.R
import com.example.azp.Task
import com.example.azp.TaskState
import com.example.azp.TaskViewModel
import com.example.azp.TaskViewModelFactory
import com.example.azp.utilities.TaskFirebaseRepository


class AddTaskActivity : AppCompatActivity() {

    private val taskRepository = TaskFirebaseRepository()
    private val model: TaskViewModel by viewModels(){
        TaskViewModelFactory(taskRepository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        val cancelBtn = findViewById<Button>(R.id.button_Cancel)
        cancelBtn.setOnClickListener {
            finish()
        }

        val saveBtn = findViewById<Button>(R.id.button_Save)
        val editTitle = findViewById<TextView>(R.id.editTextTitle)
        val editDate = findViewById<TextView>(R.id.editTextDate)
        val stateToDo = findViewById<CheckBox>(R.id.checkBox2_to_do)
        val stateInProgress = findViewById<CheckBox>(R.id.checkBox1_in_progress)
        val stateComplete = findViewById<CheckBox>(R.id.checkBox4_complete)
        val stateMilestone = findViewById<CheckBox>(R.id.checkBox4_complete)

        var selectedState: TaskState = TaskState.IN_PROGRESS

        stateToDo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedState = TaskState.TODO
                stateInProgress.isChecked = false
                stateComplete.isChecked = false
                stateMilestone.isChecked = false // Снимите флажки с других чекбоксов
            }
        }

        stateInProgress.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedState = TaskState.IN_PROGRESS
                stateToDo.isChecked = false
                stateComplete.isChecked = false
                stateMilestone.isChecked = false // Снимите флажки с других чекбоксов
            }
        }

        stateComplete.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedState = TaskState.COMPLETED
                stateToDo.isChecked = false
                stateInProgress.isChecked = false
                stateMilestone.isChecked = false // Снимите флажки с других чекбоксов
            }
        }

        stateMilestone.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedState = TaskState.MILESTONE
                stateToDo.isChecked = false
                stateInProgress.isChecked = false
                stateComplete.isChecked = false // Снимите флажки с других чекбоксов
            }
        }

        saveBtn.setOnClickListener {
            val taskTitle: String = editTitle.text.toString()
            val taskDate: String = editDate.text.toString()
            val taskDescription: String = ""
            val newTask: Task = Task(
                "",
                taskTitle,
                taskDescription,
                selectedState,
                taskDate
            )

            model.addTask(newTask)

            finish()
        }
    }
}