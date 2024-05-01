package com.example.azp.activities

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.azp.R
import java.util.Calendar

class DateItemActivity : AppCompatActivity() {
    private var datePickerDialog: DatePickerDialog? = null
    private var dateButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task) // Replace with your layout
        initDatePicker()
        dateButton = findViewById<Button>(R.id.datePickerButton) // Replace with your button ID
        dateButton!!.text = todaysDate

    }

    private val todaysDate: String
        get() {
            val cal: Calendar = Calendar.getInstance()
            val year: Int = cal.get(Calendar.YEAR)
            var month: Int = cal.get(Calendar.MONTH)
            month += 1
            val day: Int = cal.get(Calendar.DAY_OF_MONTH)
            return makeDateString(day, month, year)
        }

    private fun initDatePicker() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
                var month = month
                month += 1
                val date = makeDateString(day, month, year)
                dateButton!!.text = date
            }
        val cal: Calendar = Calendar.getInstance()
        val year: Int = cal.get(Calendar.YEAR)
        val month: Int = cal.get(Calendar.MONTH)
        val day: Int = cal.get(Calendar.DAY_OF_MONTH)
        val style: Int = AlertDialog.THEME_HOLO_LIGHT
        datePickerDialog = DatePickerDialog(this, style, dateSetListener, year, month, day)
        //datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
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
            else -> "JAN"
        }
    }

    fun openDatePicker(view: View?) {
        datePickerDialog!!.show()
    }
}
