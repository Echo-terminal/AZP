
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.azp.MainActivity
import com.example.azp.R
import com.example.azp.activities.LoginActivity
import com.example.azp.utilities.AuthRepository
import com.example.azp.utilities.AuthViewModel
import com.example.azp.utilities.AuthViewModelFactory

class ProfileFragment : Fragment() {

    private lateinit var emailTextView: TextView
    private lateinit var logOutButton: ImageButton
    private lateinit var model: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val authRepository = AuthRepository()
        val viewModelFactory = AuthViewModelFactory(authRepository)
        model = ViewModelProvider(this, viewModelFactory)[AuthViewModel::class.java]
        observeViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        emailTextView = view.findViewById(R.id.text_view_email)
        emailTextView.setOnClickListener {
            if (model.getCurrentUser().value == null) {
                val loginRequestCode = 123
                val intent = Intent(context, LoginActivity::class.java)
                startActivityForResult(intent, loginRequestCode)

                fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
                    if (requestCode == loginRequestCode && resultCode == Activity.RESULT_OK) {
                        observeViewModel()
                    }
                }
            }
        }
        logOutButton = view.findViewById(R.id.log_out)
        logOutButton.setOnClickListener {
            model.signOut()
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        model.getCurrentUser().observe(viewLifecycleOwner) { user ->
            emailTextView.text = user?.email ?: "User is not logged in"
        }
    }
}
