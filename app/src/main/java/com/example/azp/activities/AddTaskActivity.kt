package com.example.azp.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.DatePicker
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
import java.util.Calendar


class AddTaskActivity : AppCompatActivity() {

    private val taskRepository = TaskFirebaseRepository()
    private val model: TaskViewModel by viewModels {
        TaskViewModelFactory(taskRepository)
    }

    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var dateButton: Button
    private var selectedState:TaskState = TaskState.TODO
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        initDatePicker()
        dateButton = findViewById(R.id.datePickerButton) // Найдем кнопку по ее id
        dateButton.text = getTodaysDate()

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
            val taskDate = dateButton.text.toString()
            val taskDescription = editTextDescription.text.toString()
            val newTask = Task("", taskTitle, taskDescription, selectedState, taskDate)
            val resultIntent = Intent()
            val gson = Gson()
            val json = gson.toJson(newTask)
            resultIntent.putExtra("json", json)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun getTodaysDate(): String {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        return makeDateString(day, month, year)
    }

    private fun initDatePicker() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { datePicker: DatePicker, year: Int, month: Int, day: Int ->
                val adjustedMonth = month + 1
                val date = makeDateString(day, adjustedMonth, year)
                dateButton.text = date
            }

        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val style = AlertDialog.THEME_HOLO_LIGHT

        datePickerDialog = DatePickerDialog(this, style, dateSetListener, year, month, day)
    }

    private fun makeDateString(day: Int, month: Int, year: Int): String {
        return getMonthFormat(month) + " " + day + " " + year
    }

    private fun getMonthFormat(month: Int): String {
        return when (month) {
            1 -> "JAN"
            2 -> "FEB"
            3 -> "MAR"
            4 -> "APR"
            5 -> "MAY"
            6 -> "JUN"
            7 -> "JUL"
            8 -> "AUG"
            9 -> "SEP"
            10 -> "OCT"
            11 -> "NOV"
            12 -> "DEC"
            else -> "JAN" // default should never happen
        }
    }

    fun openDatePicker(view: View) {
        datePickerDialog.show()
    }

}