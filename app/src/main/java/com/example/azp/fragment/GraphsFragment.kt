package com.example.azp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ViewSwitcher
//import androidx.compose.ui.graphics.Color
import android.graphics.Color
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.azp.R
import com.example.azp.data_classes.Task
import com.example.azp.utilities.TaskFirebaseRepository
import com.example.azp.utilities.TaskViewModel
import com.example.azp.utilities.TaskViewModelFactory
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class GraphsFragment : Fragment() {

    private lateinit var taskModel: TaskViewModel
    private lateinit var viewSwitcher: ViewSwitcher

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_graphs, container, false)
        taskModel = ViewModelProvider(this, TaskViewModelFactory(TaskFirebaseRepository()))[TaskViewModel::class.java]

        viewSwitcher = view.findViewById(R.id.viewSwitcher)
        val pieChart: PieChart = view.findViewById(R.id.pieChart)
        val barChart: BarChart = view.findViewById(R.id.barChart)
        val switchButton: Button = view.findViewById(R.id.buttonSwitch)

        switchButton.setOnClickListener {
            viewSwitcher.showNext()
        }

        taskModel.getTaskType { taskTypes ->
            setupPieChart(pieChart, taskTypes)
        }


        taskModel.getAllSortedTasksOfMonth(5){ tasks->
            taskModel.getAllCompletedTasks(tasks){
                setupBarChart(barChart,it)
            }
        }

        return view
    }

    private fun setupPieChart(pieChart: PieChart, taskTypes: List<Int>) {
        val pieEntries = ArrayList<PieEntry>()
        val toDo = taskTypes[0].toFloat()
        val progress = taskTypes[1].toFloat()
        val mileStones = taskTypes[2].toFloat()
        val completed = taskTypes[3].toFloat()
        pieEntries.add(PieEntry(toDo, "To Do"))
        pieEntries.add(PieEntry(progress, "Progress"))
        pieEntries.add(PieEntry(mileStones, "Milestones"))
        pieEntries.add(PieEntry(completed, "Completed"))

        val dataSet = PieDataSet(pieEntries, "Your data")
        dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.invalidate() // refresh
    }

    private fun setupBarChart(barChart: BarChart, tasks: List<Task>) {
        val entries = ArrayList<BarEntry>()
        val sort: MutableList<Int> = mutableListOf<Int>().apply {
            for (i in 0 .. 31) add(0)
        }

        for(i in 0 until tasks.size) {
            sort[tasks[i].getDueDate().day]++

        }
        for(i in 0 until sort.size){
           entries.add(BarEntry( i.toFloat(),sort[i].toFloat() ))
        }



        val dataSet = BarDataSet(entries, "Task Types")
        dataSet.color = Color.parseColor("#0000FF")
        val barData = BarData(dataSet)
        barChart.data = barData

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawLabels(true) // Разрешить отрисовку меток
        xAxis.setDrawGridLines(false) // Отключить сетку

        // Установка меток на ось X через цикл
        val labels = ArrayList<String>()
        for (i in 0 .. 30) {
            labels.add((i + 1).toString() + " день")
        }
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)



        barChart.invalidate()
    }
}
