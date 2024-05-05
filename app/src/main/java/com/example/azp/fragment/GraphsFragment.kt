package com.example.azp.fragment
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.azp.R
import com.example.azp.data_classes.Task
import com.example.azp.utilities.TaskFirebaseRepository
import com.example.azp.utilities.TaskViewModel
import com.example.azp.utilities.TaskViewModelFactory
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.Gson
import kotlinx.coroutines.launch

class GraphsFragment : Fragment() {

    private lateinit var taskModel: TaskViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_graphs, container, false)
        taskModel = ViewModelProvider(this, TaskViewModelFactory(TaskFirebaseRepository()))[TaskViewModel::class.java]

        // Наблюдатель за изменениями в списке типов задач
        taskModel.getTaskType{taskTypes->
            Log.d("Aaaa", taskTypes.toString())
            val pieChart: PieChart = view.findViewById(R.id.pieChart)
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

            dataSet.colors = ColorTemplate.COLORFUL_COLORS.asList()

            val data = PieData(dataSet)
            pieChart.data = data
            pieChart.invalidate() // refresh




        }

        return view
    }
}