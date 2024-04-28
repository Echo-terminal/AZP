package com.example.azp.fragment

import TaskDetailsDialogFragment
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.azp.R
import com.example.azp.activities.AddTaskActivity
import com.example.azp.data_classes.Task
import com.example.azp.data_classes.TaskAdapter
import com.example.azp.data_classes.TaskState
import com.example.azp.utilities.TaskFirebaseRepository
import com.example.azp.utilities.TaskViewModel
import com.example.azp.utilities.TaskViewModelFactory
import com.google.gson.Gson


class ListFragment : Fragment() {

    private lateinit var taskModel: TaskViewModel
    private lateinit var recyclerViewToDo: RecyclerView
    private lateinit var recyclerViewInProgress: RecyclerView
    private lateinit var recyclerViewCompleted: RecyclerView
    private lateinit var recyclerViewMilestones: RecyclerView

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val json = data?.getStringExtra("json")

            if (json != null) {
                val gson = Gson()
                val task = gson.fromJson(json, Task::class.java)
                taskModel.addTask(task)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_list, container, false)

        val buttonInProgress = view.findViewById<Button>(R.id.buttonInProgress)
        val buttonToDo = view.findViewById<Button>(R.id.buttonToDo)
        val buttonMilestones = view.findViewById<Button>(R.id.buttonMilestones)
        val buttonComplete = view.findViewById<Button>(R.id.buttonComplete)

        val layoutToDo = view.findViewById<ConstraintLayout>(R.id.constraintLayout2)
        val layoutInProgress = view.findViewById<ConstraintLayout>(R.id.constraintLayout1)
        val layoutMilestone = view.findViewById<ConstraintLayout>(R.id.constraintLayout3)
        val layoutComplete = view.findViewById<ConstraintLayout>(R.id.constraintLayout4)

        buttonInProgress.setOnClickListener {
            layoutInProgress.visibility = View.VISIBLE
            layoutToDo.visibility = View.GONE
            layoutComplete.visibility = View.GONE
            layoutMilestone.visibility = View.GONE
        }

        buttonToDo.setOnClickListener {
            layoutInProgress.visibility = View.GONE
            layoutToDo.visibility = View.VISIBLE
            layoutComplete.visibility = View.GONE
            layoutMilestone.visibility = View.GONE
        }

        buttonMilestones.setOnClickListener {
            layoutInProgress.visibility = View.GONE
            layoutToDo.visibility = View.GONE
            layoutComplete.visibility = View.GONE
            layoutMilestone.visibility = View.VISIBLE
        }

        buttonComplete.setOnClickListener {
            layoutInProgress.visibility = View.GONE
            layoutToDo.visibility = View.GONE
            layoutComplete.visibility = View.VISIBLE
            layoutMilestone.visibility = View.GONE
        }



        val addButton = view.findViewById<ImageButton>(R.id.addButton1)

        recyclerViewInProgress = view.findViewById<RecyclerView>(R.id.recyclerViewInProgress)
        recyclerViewToDo = view.findViewById<RecyclerView>(R.id.recyclerViewTodo)
        recyclerViewCompleted = view.findViewById<RecyclerView>(R.id.recyclerViewComplete)
        recyclerViewMilestones = view.findViewById<RecyclerView>(R.id.recyclerViewMilestones)

        val layoutManagerToDo = LinearLayoutManager(requireContext())
        val layoutManagerInProgress = LinearLayoutManager(requireContext())
        val layoutManagerCompleted = LinearLayoutManager(requireContext())
        val layoutManagerMilestones = LinearLayoutManager(requireContext())

        recyclerViewInProgress.layoutManager = layoutManagerInProgress
        recyclerViewToDo.layoutManager = layoutManagerToDo
        recyclerViewCompleted.layoutManager = layoutManagerCompleted
        recyclerViewMilestones.layoutManager = layoutManagerMilestones

        taskModel = ViewModelProvider(this, TaskViewModelFactory(TaskFirebaseRepository()))[TaskViewModel::class.java]

        updateAdapterData()

        addButton.setOnClickListener{
            val intent = Intent(context, AddTaskActivity::class.java)
            startForResult.launch(intent)
        }



        return view
    }



    private fun updateAdapterData() {
        taskModel.getAllTasks().observe(viewLifecycleOwner) { tasks ->
            val todoTasks = tasks.filter { it.getState() == TaskState.TODO }
            val inProgressTasks = tasks.filter { it.getState() == TaskState.IN_PROGRESS }
            val completedTasks = tasks.filter { it.getState() == TaskState.COMPLETED }
            val milestoneTasks = tasks.filter { it.getState() == TaskState.MILESTONE }

            recyclerViewInProgress.adapter = TaskAdapter(inProgressTasks).apply {
                setOnItemClickListener { task ->

                    val taskJson = Gson().toJson(task)
                    val dialogFragment = TaskDetailsDialogFragment.newInstance(taskJson)
                    dialogFragment.show(parentFragmentManager, "TaskDetailsDialogFragment")
                }
            }

            recyclerViewToDo.adapter = TaskAdapter(todoTasks).apply {
                setOnItemClickListener { task ->

                    val taskJson = Gson().toJson(task)
                    val dialogFragment = TaskDetailsDialogFragment.newInstance(taskJson)
                    dialogFragment.show(parentFragmentManager, "TaskDetailsDialogFragment")
                }
            }

            recyclerViewCompleted.adapter = TaskAdapter(completedTasks).apply {
                setOnItemClickListener { task ->

                    val taskJson = Gson().toJson(task)
                    val dialogFragment = TaskDetailsDialogFragment.newInstance(taskJson)
                    dialogFragment.show(parentFragmentManager, "TaskDetailsDialogFragment")
                }
            }
            recyclerViewMilestones.adapter = TaskAdapter(milestoneTasks).apply {
                setOnItemClickListener { task ->

                    val taskJson = Gson().toJson(task)
                    val dialogFragment = TaskDetailsDialogFragment.newInstance(taskJson)
                    dialogFragment.show(parentFragmentManager, "TaskDetailsDialogFragment")
                }
            }
        }
    }

}