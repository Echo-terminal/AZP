// CalendarFragment.kt
package com.example.azp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.azp.R
import com.example.azp.data_classes.Date
import com.example.azp.data_classes.Task
import com.example.azp.utilities.CalendarAdapter
import com.example.azp.utilities.OnItemListener
import com.example.azp.utilities.TaskAdapter
import com.example.azp.utilities.TaskFirebaseRepository
import com.example.azp.utilities.TaskViewModel
import com.example.azp.utilities.TaskViewModelFactory
import com.google.gson.Gson
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class CalendarFragment : Fragment(), OnItemListener, TaskDetailsDialogFragment.TaskDetailsListener {

    private lateinit var monthYearText: TextView
    private lateinit var calendarRecyclerView: RecyclerView
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var taskModel: TaskViewModel
    private var selectedDate = LocalDate.now()
    private lateinit var taskList: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)
        initWidgets(view)
        taskModel = ViewModelProvider(this, TaskViewModelFactory(TaskFirebaseRepository()))[TaskViewModel::class.java]

        val previousButton = view.findViewById<Button>(R.id.previousMonthAction)
        val nextButton = view.findViewById<Button>(R.id.nextMonthAction)

        previousButton.setOnClickListener {
            previousMonthAction()
        }

        nextButton.setOnClickListener {
            nextMonthAction()
        }

        setMonthView()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskList = view.findViewById<RecyclerView>(R.id.taskListOfMonth)
        val layoutManager = LinearLayoutManager(requireContext())
        taskList.layoutManager = layoutManager
        observeTaskList()
    }

    private fun observeTaskList(){
        taskModel.getAllSortedTasksOfMonth(selectedDate.monthValue){tasks ->
            taskList.adapter = TaskAdapter(tasks).apply {
                setOnItemClickListener {task ->
                    val taskJson = Gson().toJson(task)
                    val dialogFragment = TaskDetailsDialogFragment.newInstance(taskJson)
                    dialogFragment.setTargetFragment(this@CalendarFragment, 0)
                    dialogFragment.show(parentFragmentManager, "taskDetailsDialog")}
            }
        }
    }

    private fun initWidgets(view: View) {
        monthYearText = view.findViewById(R.id.monthYearTV)
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView)
    }

    private fun setMonthView() {
        monthYearText.text = monthYearFromDate(selectedDate)
        val daysInMonth = daysInMonthArray(selectedDate)

        // Используем переменную класса calendarAdapter
        calendarAdapter = CalendarAdapter(daysInMonth, this)

        calendarRecyclerView.layoutManager = GridLayoutManager(requireContext(), 7)
        calendarRecyclerView.adapter = calendarAdapter
    }

    private fun daysInMonthArray(date: LocalDate): ArrayList<String> {
        val daysInMonthArray = ArrayList<String>()
        val yearMonth = YearMonth.from(date)
        val daysInMonth = yearMonth.lengthOfMonth()
        val firstOfMonth = selectedDate.withDayOfMonth(1)
        val dayOfWeek = firstOfMonth.dayOfWeek.value

        for (i in 1..42) {
            if (i <= dayOfWeek || i > daysInMonth + dayOfWeek) {
                daysInMonthArray.add("")
            } else {
                daysInMonthArray.add((i - dayOfWeek).toString())
            }
        }
        return daysInMonthArray
    }

    private fun monthYearFromDate(date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
        return date.format(formatter)
    }

    override fun onItemClick(position: Int, dayText: String) {
        if (dayText.isNotEmpty()) {
            val selectedDateFormat = Date(selectedDate.year, selectedDate.monthValue, dayText.toInt())
            val dateJson = Gson().toJson(selectedDateFormat)
            val dialogFragment = DateDetailsDialogFragment.newInstance(dateJson)
            dialogFragment.show(parentFragmentManager, "DateDetailsDialogFragment")
        }
    }

    private fun previousMonthAction() {
        selectedDate = selectedDate.minusMonths(1)
        setMonthView()
        observeTaskList()
    }

    private fun nextMonthAction() {
        selectedDate = selectedDate.plusMonths(1)
        setMonthView()
        observeTaskList()
    }


    override fun onTaskUpdated(task: Task) {
        // Обработка обновленной задачи
        taskModel.updateTask(task)
        observeTaskList() // Обновление данных в адаптере
    }

    override fun onTaskDeleted(taskId: String) {
        // Обработка удаления задачи
        taskModel.deleteTask(taskId)
        observeTaskList() // Обновление данных в адаптере
    }

}

