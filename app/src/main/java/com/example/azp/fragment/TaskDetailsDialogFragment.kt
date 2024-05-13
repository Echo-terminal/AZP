import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.azp.data_classes.Date
import com.example.azp.data_classes.Task
import com.example.azp.data_classes.TaskState
import com.example.azp.databinding.TaskDetailsFragmentBinding
import com.example.azp.utilities.TaskFirebaseRepository
import com.example.azp.utilities.TaskViewModel
import com.example.azp.utilities.TaskViewModelFactory
import com.google.gson.Gson
import java.util.Calendar

class TaskDetailsDialogFragment : DialogFragment() {
    private lateinit var viewModel: TaskViewModel
    private var _binding: TaskDetailsFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var dateButton: Button
    private lateinit var taskDate: Date

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
        viewModel = ViewModelProvider(this, TaskViewModelFactory(TaskFirebaseRepository()))[TaskViewModel::class.java]
        task?.let { displayTaskDetails(it) }
        binding.buttonCompleted.setOnClickListener{
            task.setTaskState(TaskState.COMPLETED)
            viewModel.updateTask(task)
            dismiss()
        }
        binding.buttonSave.setOnClickListener{
            val title = binding.taskDetailsTitle.text
            val description = binding.taskDetailsDescription.text
            if (task.getTitle()!=title.toString()) task.setTitle(title.toString())
            if (task.getDescription()!=description.toString()) task.setDescription(description.toString())
            if (task.getDueDate()!=taskDate) task.setDate(taskDate)
            viewModel.updateTask(task)
            dismiss()
        }
        initDatePicker() // Инициализация DatePicker
        binding.taskDetailsDate.setOnClickListener{
            openDatePicker(it) // Открытие DatePicker
        }
        binding.buttonDelete.setOnClickListener{
            viewModel.deleteTask(task.getId())
            dismiss()
        }
        // Установка обработчика для кнопки закрытия диалогового окна
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
