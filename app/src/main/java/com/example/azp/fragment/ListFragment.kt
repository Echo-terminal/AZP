package com.example.azp.fragment

import TaskAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.azp.R
import com.example.azp.activities.AddTaskActivity
import com.example.azp.data_classes.TaskState
import com.example.azp.utilities.TaskFirebaseRepository
import com.example.azp.utilities.TaskViewModel
import com.example.azp.utilities.TaskViewModelFactory


class ListFragment : Fragment() {

    private lateinit var taskModel: TaskViewModel
    private lateinit var recyclerViewToDo: RecyclerView
    private lateinit var recyclerViewInProgress: RecyclerView
    private lateinit var recyclerViewCompleted: RecyclerView
    private lateinit var recyclerViewMilestones: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view = inflater.inflate(R.layout.fragment_list, container, false)

        val b_in_progress = view.findViewById<Button>(R.id.button4)
        val b_to_do = view.findViewById<Button>(R.id.button5)
        val b_milestones = view.findViewById<Button>(R.id.button6)
        val b_complete = view.findViewById<Button>(R.id.button7)

        b_in_progress.setOnClickListener{
            Toast.makeText(requireContext(), "b_in_progress clicked", Toast.LENGTH_SHORT).show()
        }

        b_to_do.setOnClickListener{
            Toast.makeText(requireContext(), "b_to_do clicked", Toast.LENGTH_SHORT).show()
        }

        b_milestones.setOnClickListener{
            Toast.makeText(requireContext(), "b_milestones clicked", Toast.LENGTH_SHORT).show()
        }

        b_complete.setOnClickListener{
            Toast.makeText(requireContext(), "b_complete clicked", Toast.LENGTH_SHORT).show()
        }


        val addButton = view.findViewById<ImageButton>(R.id.addButton1)

        recyclerViewInProgress = view.findViewById<RecyclerView>(R.id.recyclerView_1)
        recyclerViewToDo = view.findViewById<RecyclerView>(R.id.recyclerView_2)
        recyclerViewCompleted = view.findViewById<RecyclerView>(R.id.recyclerView_4)
        recyclerViewMilestones = view.findViewById<RecyclerView>(R.id.recyclerView_3)

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
            val intent = Intent(context,AddTaskActivity::class.java)
            startActivity(intent)
        }



        return view
    }

    fun updateAdapterData() {
        taskModel.getAllTasks().observe(viewLifecycleOwner) { tasks ->
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