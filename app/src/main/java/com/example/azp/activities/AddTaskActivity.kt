package com.example.azp.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.azp.R
import com.example.azp.data_classes.Date
import com.example.azp.data_classes.Task
import com.example.azp.data_classes.TaskState
import com.example.azp.utilities.TaskFirebaseRepository
import com.example.azp.utilities.TaskViewModel
import com.example.azp.utilities.TaskViewModelFactory
import com.google.gson.Gson
import java.util.Calendar


class AddTaskActivity : AppCompatActivity() {

    private lateinit var taskDateTextView: TextView
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var taskDate: Date
    private lateinit var dateCompleted: Date
    private var selectedState:TaskState = TaskState.NONE
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)
        taskDate = Date.now()
        dateCompleted = Date(1,1,1)

        initDatePicker()
        taskDateTextView = findViewById(R.id.currentDate)

        taskDateTextView.text = taskDate.toString()
        taskDateTextView.setOnClickListener {
            openDatePicker()
        }

        val cancelBtn = findViewById<Button>(R.id.button_Cancel)
        cancelBtn.setOnClickListener {
            finish()
        }

        val saveBtn = findViewById<Button>(R.id.button_Save)
        val editTitle = findViewById<TextView>(R.id.editTextTitle)
        val editTextDescription = findViewById<TextView>(R.id.editTextDescription) //поле описания
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
            val taskDescription = editTextDescription.text.toString()
            val newTask = Task("", taskTitle, taskDescription, selectedState, taskDate, dateCompleted)
            val resultIntent = Intent()
            val gson = Gson()
            val json = gson.toJson(newTask)
            resultIntent.putExtra("json", json)
            if(taskTitle != "" && selectedState != TaskState.NONE){
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Empty task", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getTodayDate(): Date {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        return Date(year, month, day)
    }

    private fun initDatePicker() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, day: Int ->
                val adjustedMonth = month + 1
                taskDate = Date(year, adjustedMonth, day) // Создаем объект Date
                taskDateTextView.text = taskDate.toString() // Устанавливаем выбранную дату на кнопку
            }

        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val style = AlertDialog.THEME_HOLO_LIGHT

        datePickerDialog = DatePickerDialog(this, style, dateSetListener, year, month, day)
    }

    private fun openDatePicker() {
        datePickerDialog.show()
    }

}