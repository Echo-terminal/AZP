import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.azp.R
import com.example.azp.adapter.FileAdapter
import com.example.azp.data_classes.Date
import com.example.azp.data_classes.Task
import com.example.azp.data_classes.TaskState
import com.example.azp.databinding.TaskDetailsFragmentBinding
import com.example.azp.utilities.TaskFirebaseRepository
import com.example.azp.utilities.TaskViewModel
import com.example.azp.utilities.TaskViewModelFactory
import com.example.azp.viewmodel.DocumentsViewModel
import com.google.gson.Gson
import java.util.Calendar

class TaskDetailsDialogFragment : DialogFragment() {

    private var _binding: TaskDetailsFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var taskDate: Date
    private lateinit var fileAdapter: FileAdapter
    private lateinit var documentsViewModel: DocumentsViewModel

    interface TaskDetailsListener {
        fun onTaskUpdated(task: Task)
        fun onTaskDeleted(taskId: String)
    }

    private var listener: TaskDetailsListener? = null

    companion object {
        private const val ARG_TASK_JSON = "task_json"

        fun newInstance(taskJson: String): TaskDetailsDialogFragment {
            val fragment = TaskDetailsDialogFragment()
            val args = Bundle()
            args.putString(ARG_TASK_JSON, taskJson)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = targetFragment as? TaskDetailsListener
        if (listener == null) {
            throw ClassCastException("$context must implement TaskDetailsListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TaskDetailsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val taskJson = arguments?.getString(ARG_TASK_JSON)
        val gson = Gson()
        val task = gson.fromJson(taskJson, Task::class.java)
        taskDate = task.getDueDate()
        task?.let { displayTaskDetails(it) }
        binding.buttonCompleted.setOnClickListener{
            task.setTaskState(TaskState.COMPLETED)
            task.setDateCom(Date.now())
            listener?.onTaskUpdated(task)
            dismiss()
        }
        binding.buttonSave.setOnClickListener{
            val title = binding.taskDetailsTitle.text.toString()
            val description = binding.taskDetailsDescription.text.toString()
            if (task.getTitle() != title) task.setTitle(title)
            if (task.getDescription() != description) task.setDescription(description)
            if (task.getDueDate() != taskDate) task.setDate(taskDate)
            listener?.onTaskUpdated(task) // Передача данных
            dismiss()
        }
        initDatePicker() // Инициализация DatePicker
        binding.taskDetailsDate.setOnClickListener{
            openDatePicker(it) // Открытие DatePicker
        }
        binding.buttonDelete.setOnClickListener{
            listener?.onTaskDeleted(task.getId()) // Передача данных
            dismiss()
        }

        val recyclerView: RecyclerView = view.findViewById(R.id.task_details_files)
        recyclerView.layoutManager = LinearLayoutManager(context)
        fileAdapter = FileAdapter(documentsViewModel.fileList.value ?: mutableListOf(), requireContext()) // передаем контекст в адаптер
        recyclerView.adapter = fileAdapter

        documentsViewModel.fileList.observe(viewLifecycleOwner, { files ->
            fileAdapter.updateFiles(files)
        })
    }

    private fun displayTaskDetails(task: Task) {
        binding.taskDetailsTitle.text = Editable.Factory.getInstance().newEditable(task.getTitle())
        binding.taskDetailsDate.text = task.getDueDate().toString()
        binding.taskDetailsDescription.text = Editable.Factory.getInstance().newEditable(task.getDescription())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initDatePicker() {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val style = AlertDialog.THEME_HOLO_LIGHT

        datePickerDialog = DatePickerDialog(requireContext(), style, { _: DatePicker, year: Int, month: Int, day: Int ->
            val adjustedMonth = month + 1
            taskDate = Date(year, adjustedMonth, day) // Создаем объект Date
            binding.taskDetailsDate.text = taskDate.toString() // Устанавливаем выбранную дату на TextView
        }, year, month, day)
    }

    private fun openDatePicker(view: View) {
        datePickerDialog.show()
    }
}
