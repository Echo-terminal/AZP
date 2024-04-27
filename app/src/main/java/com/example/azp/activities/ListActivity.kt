package com.example.azp.activities

import TaskAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.azp.R
import com.example.azp.data_classes.TaskState
import com.example.azp.utilities.TaskFirebaseRepository
import com.example.azp.utilities.TaskViewModel
import com.example.azp.utilities.TaskViewModelFactory

class ListActivity: AppCompatActivity() {
    private val taskRepository = TaskFirebaseRepository()
    private val taskModel: TaskViewModel by viewModels {
        TaskViewModelFactory(taskRepository)
    }

    private lateinit var recyclerViewToDo: RecyclerView
    private lateinit var recyclerViewInProgress: RecyclerView
    private lateinit var recyclerViewCompleted: RecyclerView
    private lateinit var recyclerViewMilestones: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_list)

        val buttonInProgress = findViewById<Button>(R.id.buttonInProgress)
        val buttonToDo = findViewById<Button>(R.id.buttonToDo)
        val buttonMilestones = findViewById<Button>(R.id.buttonMilestones)
        val buttonComplete = findViewById<Button>(R.id.buttonComplete)

        val layoutToDo = findViewById<ConstraintLayout>(R.id.constraintLayout2)
        val layoutInProgress = findViewById<ConstraintLayout>(R.id.constraintLayout1)
        val layoutMilestone = findViewById<ConstraintLayout>(R.id.constraintLayout3)
        val layoutComplete = findViewById<ConstraintLayout>(R.id.constraintLayout4)

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



        val addButton = findViewById<ImageButton>(R.id.addButton1)

        recyclerViewInProgress = findViewById(R.id.recyclerViewInProgress)
        recyclerViewToDo = findViewById(R.id.recyclerViewTodo)
        recyclerViewCompleted = findViewById(R.id.recyclerViewComplete)
        recyclerViewMilestones = findViewById(R.id.recyclerViewMilestones)

        val layoutManagerToDo = LinearLayoutManager(this)
        val layoutManagerInProgress = LinearLayoutManager(this)
        val layoutManagerCompleted = LinearLayoutManager(this)
        val layoutManagerMilestones = LinearLayoutManager(this)

        recyclerViewInProgress.layoutManager = layoutManagerInProgress
        recyclerViewToDo.layoutManager = layoutManagerToDo
        recyclerViewCompleted.layoutManager = layoutManagerCompleted
        recyclerViewMilestones.layoutManager = layoutManagerMilestones

        updateAdapterData()

        addButton.setOnClickListener{
            val intent = Intent(this,AddTaskActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateAdapterData() {
        taskModel.getAllTasks().observe(this) { tasks ->
            val todoTasks = tasks.filter { it.getState() == TaskState.TODO }
            val inProgressTasks = tasks.filter { it.getState() == TaskState.IN_PROGRESS }
            val completedTasks = tasks.filter { it.getState() == TaskState.COMPLETED }
            val milestoneTasks = tasks.filter { it.getState() == TaskState.MILESTONE }

            recyclerViewInProgress.adapter = TaskAdapter(inProgressTasks)
            recyclerViewToDo.adapter = TaskAdapter(todoTasks)
            recyclerViewCompleted.adapter = TaskAdapter(completedTasks)
            recyclerViewMilestones.adapter = TaskAdapter(milestoneTasks)
        }
    }

}