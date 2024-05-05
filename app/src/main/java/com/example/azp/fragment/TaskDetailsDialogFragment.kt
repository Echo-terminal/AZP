
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.azp.data_classes.Task
import com.example.azp.databinding.TaskDetailsFragmentBinding
import com.google.gson.Gson

class TaskDetailsDialogFragment : DialogFragment() {

    private var _binding: TaskDetailsFragmentBinding? = null
    private val binding get() = _binding!!

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

        task?.let { displayTaskDetails(it) }

        // Установка обработчика для кнопки закрытия диалогового окна
    }

    private fun displayTaskDetails(task: Task) {
        binding.taskDetailsTitle.text = task.getTitle()
        binding.taskDetailsDate.text = task.getDueDate().toString()
        binding.taskDetailsDescription.text = task.getDescription()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
