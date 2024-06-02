package com.example.azp.fragment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.azp.R
import com.example.azp.data_classes.Date
import com.example.azp.data_classes.Task
import com.example.azp.utilities.TaskFirebaseRepository
import com.example.azp.utilities.TaskViewModel
import com.example.azp.utilities.TaskViewModelFactory
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.util.Calendar

class GraphsFragment : Fragment() {

    private lateinit var taskModel: TaskViewModel
    private var startDate: Date? = null
    private var endDate: Date? = null
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var barChart: BarChart
    private var isStartDatePicker: Boolean = true // Для определения какой DatePicker открыт

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_graphs, container, false)
        taskModel = ViewModelProvider(this, TaskViewModelFactory(TaskFirebaseRepository()))[TaskViewModel::class.java]

        showDatePickerDialog()

        val pieChart: PieChart = view.findViewById(R.id.pieChart)
        barChart = view.findViewById(R.id.barChartq)
        val button1: Button = view.findViewById(R.id.button_show_date_picker1)
        val button2: Button = view.findViewById(R.id.button_show_date_picker2)
        val button3: Button = view.findViewById(R.id.button_go)

        button1.setOnClickListener {
            isStartDatePicker = true
            openDatePicker(button1)
        }

        button2.setOnClickListener {
            isStartDatePicker = false
            openDatePicker(button2)
        }

        button3.setOnClickListener {
            updateBarChartWithSelectedDate()
        }

        taskModel.getTaskType { taskTypes ->
            setupPieChart(pieChart, taskTypes)
        }

        return view
    }

    private fun setupPieChart(pieChart: PieChart, taskTypes: List<Int>) {
        val pieEntries = ArrayList<PieEntry>()
        pieEntries.add(PieEntry(taskTypes[0].toFloat(), "To Do"))
        pieEntries.add(PieEntry(taskTypes[1].toFloat(), "Progress"))
        pieEntries.add(PieEntry(taskTypes[2].toFloat(), "Milestones"))
        pieEntries.add(PieEntry(taskTypes[3].toFloat(), "Completed"))

        val dataSet = PieDataSet(pieEntries, "Task Types")
        dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.invalidate() // refresh
    }

    private fun setupBarChart(barChart: BarChart, tasks: List<Task>) {
        val entries = ArrayList<BarEntry>()
        val startDate = startDate ?: return
        val endDate = endDate ?: return
        val s = endDate - startDate
        val sort = MutableList(s) { 0 }

        tasks.forEach { task ->
            val ind = s - (endDate - task.getDateCom())
            if (ind in sort.indices) {
                sort[ind]++
            }
        }

        sort.forEachIndexed { index, count ->
            entries.add(BarEntry(index.toFloat(), count.toFloat()))
        }

        val dataSet = BarDataSet(entries, "").apply {
            color = Color.parseColor("#008000") // Цвет столбцов
            valueTextSize = 6f // Размер текста значений
            valueTextColor = Color.BLACK // Цвет текста значений
            setDrawValues(false) // Отключает значения по умолчанию
        }

        val barData = BarData(dataSet)
        barChart.data = barData

        barChart.apply {
            description.isEnabled = false
            setFitBars(true)
            animateY(1000)

            axisLeft.apply {
                isEnabled = false // Отключаем левую ось
                axisMinimum = 0f // Устанавливаем минимум на 0, чтобы столбцы прилегали к нижней линии
            }

            axisRight.isEnabled = false // Отключаем правую ось

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                textSize = 12f
                textColor = Color.BLACK
                setDrawLabels(true)
                setDrawGridLines(false)
                valueFormatter = IndexAxisValueFormatter((1..s).map { "$it Day" })
            }

            legend.isEnabled = false // Отключаем легенду
        }

        barChart.invalidate() // refresh
    }

    private fun showDatePickerDialog() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, day: Int ->
            val adjustedMonth = month + 1
            val selectedDate = Date(year, adjustedMonth, day) // Создаем объект Date

            if (isStartDatePicker) {
                startDate = selectedDate
                Log.d("GraphsFragment", "Start date selected: $startDate")
            } else {
                endDate = selectedDate
                Log.d("GraphsFragment", "End date selected: $endDate")
            }
        }

        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val style = AlertDialog.THEME_HOLO_LIGHT

        datePickerDialog = DatePickerDialog(requireContext(), style, dateSetListener, year, month, day)
    }

    private fun openDatePicker(button: Button) {
        datePickerDialog.setOnDateSetListener { _: DatePicker, year: Int, month: Int, day: Int ->
            val adjustedMonth = month + 1
            val taskDate = Date(year, adjustedMonth, day) // Создаем объект Date
            // Обновляем текст кнопки
            updateButtonText(button, taskDate)

            if (isStartDatePicker) {
                startDate = taskDate
                Log.d("GraphsFragment", "Start date selected: $startDate")
            } else {
                endDate = taskDate
                Log.d("GraphsFragment", "End date selected: $endDate")
            }
        }
        datePickerDialog.show()
    }

    private fun updateBarChartWithSelectedDate() {
        val startDate = startDate
        val endDate = endDate

        if (startDate == null || endDate == null) {
            Log.e("GraphsFragment", "Start date or end date not set")
            return
        }

        taskModel.getCompletedTasksInRange(startDate, endDate) { list ->
            setupBarChart(barChart, list)
        }
    }

    private fun updateButtonText(button: Button, date: Date) {
        button.text = "${date.day}/${date.month}/${date.year}"
    }

}
