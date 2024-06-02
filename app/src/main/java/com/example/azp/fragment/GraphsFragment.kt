package com.example.azp.fragment

import android.os.Bundle
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.azp.R
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

class GraphsFragment : Fragment() {

    private lateinit var taskModel: TaskViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_graphs, container, false)
        taskModel = ViewModelProvider(this, TaskViewModelFactory(TaskFirebaseRepository()))[TaskViewModel::class.java]

        val pieChart: PieChart = view.findViewById(R.id.pieChart)
        val barChart: BarChart = view.findViewById(R.id.barChart)

        taskModel.getTaskType { taskTypes ->
            setupPieChart(pieChart, taskTypes)
        }

        taskModel.getAllSortedTasksOfMonth(5) { tasks ->
            taskModel.getAllCompletedTasks(tasks) {
                setupBarChart(barChart, it)
            }
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
        val sort = MutableList(31) { 0 }

        tasks.forEach { task ->
            val day = task.getDueDate().day
            sort[day]++
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
                valueFormatter = IndexAxisValueFormatter((1..31).map { "$it день" })
            }

            legend.isEnabled = false // Отключаем легенду

        }

        barChart.invalidate() // refresh
    }
}
