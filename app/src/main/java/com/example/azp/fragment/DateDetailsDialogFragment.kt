package com.example.azp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.azp.R
import com.example.azp.data_classes.Date
import com.example.azp.databinding.DateDetailsDialogFragmentBinding
import com.example.azp.utilities.TaskAdapter
import com.example.azp.utilities.TaskFirebaseRepository
import com.example.azp.utilities.TaskViewModel
import com.example.azp.utilities.TaskViewModelFactory
import com.google.gson.Gson

class DateDetailsDialogFragment: DialogFragment() {

    private var _binding: DateDetailsDialogFragmentBinding? = null
    private lateinit var taskModel: TaskViewModel
    private val binding get() = _binding!!
    companion object {
        private const val ARG_DATE_JSON = "date_json"

        fun newInstance(dateJson: String): DateDetailsDialogFragment {
            val fragment = DateDetailsDialogFragment()
            val args = Bundle()
            args.putString(ARG_DATE_JSON, dateJson)
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.date_details_dialog_fragment, container, false)
        _binding = DateDetailsDialogFragmentBinding.inflate(inflater, container, false)
        taskModel = ViewModelProvider(this, TaskViewModelFactory(TaskFirebaseRepository()))[TaskViewModel::class.java]
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dateJson = arguments?.getString(ARG_DATE_JSON)
        val gson = Gson()
        val date = gson.fromJson(dateJson, Date::class.java)

        val dateTextView = view.findViewById<TextView>(R.id.Date)
        val recyclerViewTaskList = view.findViewById<RecyclerView>(R.id.dateDetailsTaskList)
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerViewTaskList.layoutManager = layoutManager

        dateTextView.text = date.toString()
        taskModel.getAllTasksByDate(date){tasks ->
            recyclerViewTaskList.adapter = TaskAdapter(tasks).apply {
                setOnItemClickListener {task ->
                    val taskJson = Gson().toJson(task)
                    val dialogFragment = TaskDetailsDialogFragment.newInstance(taskJson)
                    dialogFragment.show(parentFragmentManager, "TaskDetailsDialogFragment")

                }
            }
        }
    }
}