
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.azp.AuthViewModel
import com.example.azp.R
import com.example.azp.activities.LoginActivity

class ProfileFragment : Fragment() {

    private lateinit var emailTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emailTextView = view.findViewById(R.id.text_view_email) // Идентификатор вашего TextView

        val model = ViewModelProvider(requireActivity())[AuthViewModel::class.java]

        emailTextView.text = model.emailLiveData.toString()

        emailTextView.setOnClickListener {
            if (model.userLiveData.value == null) {
                // Перейти на страницу входа/регистрации
                val intent = Intent(context, LoginActivity::class.java) // Замените LoginActivity на ваш класс активности входа
                startActivity(intent)
            }
        }


    }
}
