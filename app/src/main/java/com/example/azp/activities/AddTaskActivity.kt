package com.example.azp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.azp.R
import com.example.azp.data_classes.Task
import com.example.azp.data_classes.TaskState
import com.example.azp.utilities.TaskFirebaseRepository
import com.example.azp.utilities.TaskViewModel
import com.example.azp.utilities.TaskViewModelFactory
import com.google.gson.Gson


class AddTaskActivity : AppCompatActivity() {

    private val taskRepository = TaskFirebaseRepository()
    private val model: TaskViewModel by viewModels {
        TaskViewModelFactory(taskRepository)
    }

    private var selectedState:TaskState = TaskState.TODO
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        val cancelBtn = findViewById<Button>(R.id.button_Cancel)
        cancelBtn.setOnClickListener {
            finish()
        }

        val saveBtn = findViewById<Button>(R.id.button_Save)
        val editTitle = findViewById<TextView>(R.id.editTextTitle)
        //val editDate = findViewById<TextView>(R.id.editTextDate)
        val stateToDo = findViewById<CheckBox>(R.id.checkBox2_to_do)
        val stateInProcess = findViewById<CheckBox>(R.id.checkBox1_in_progress)
        val stateComplete = findViewById<CheckBox>(R.id.checkBox4_complete)
        val stateMilestone = findViewById<CheckBox>(R.id.checkBox3_milestones)

        stateToDo.setOnCheckedChangeListener { _ , isChecked ->
            if (isChecked) {
                selectedState = TaskState.TODO

                listOf(stateMilestone, stateInProcess, stateComplete)
                    .forEach { it.isChecked = false }
            }
        }
        stateInProcess.setOnCheckedChangeListener { _ , isChecked ->
            if (isChecked) {
                selectedState = TaskState.IN_PROGRESS

                listOf(stateToDo, stateMilestone, stateComplete)
                    .forEach { it.isChecked = false }
            }
        }
        stateComplete.setOnCheckedChangeListener { _ , isChecked ->
            if (isChecked) {
                selectedState = TaskState.COMPLETED

                listOf(stateToDo, stateInProcess, stateMilestone)
                    .forEach { it.isChecked = false }
            }
        }
        stateMilestone.setOnCheckedChangeListener { _ , isChecked ->
            if (isChecked) {
                selectedState = TaskState.MILESTONE

                listOf(stateToDo, stateInProcess, stateComplete)
                    .forEach { it.isChecked = false }
            }
        }

        saveBtn.setOnClickListener {
            val taskTitle = editTitle.text.toString()
            val taskDate = ""
            val newTask = Task("", taskTitle, "", selectedState, taskDate)

            val resultIntent = Intent()
            val gson = Gson()
            val json = gson.toJson(newTask)

            resultIntent.putExtra("json", json)

            setResult(Activity.RESULT_OK, resultIntent)

            finish()
        }
    }
}