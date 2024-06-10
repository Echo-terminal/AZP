

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.azp.R
import com.example.azp.activities.EditProfile
import com.example.azp.activities.LoginActivity
import com.example.azp.data_classes.User
import com.example.azp.utilities.AuthRepository
import com.example.azp.utilities.AuthViewModel
import com.example.azp.utilities.AuthViewModelFactory
import com.example.azp.utilities.TaskFirebaseRepository
import com.example.azp.utilities.TaskViewModel
import com.example.azp.utilities.TaskViewModelFactory
import com.google.gson.Gson

class ProfileFragment : Fragment() {

    private lateinit var emailTextView: TextView
    private lateinit var userNameTextView: TextView
    private lateinit var logOutButton: ImageButton
    private lateinit var model: AuthViewModel
    private lateinit var firebaseModel: TaskViewModel

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val json = data?.getStringExtra("json")

                if (json != null) {
                    val gson = Gson()
                    val user = gson.fromJson(json, User::class.java)
                    firebaseModel.updateUser(user)

                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onActivityCreated(savedInstanceState)
        val authRepository = AuthRepository()
        model = ViewModelProvider(this, AuthViewModelFactory(AuthRepository()))[AuthViewModel::class.java]
        firebaseModel = ViewModelProvider(this, TaskViewModelFactory(TaskFirebaseRepository()))[TaskViewModel::class.java]

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        emailTextView = view.findViewById(R.id.profile_page_email)
        userNameTextView = view.findViewById(R.id.profile_page_name)

        observeViewModel()

        val editButton = view.findViewById<ImageButton>(R.id.edit_button)

        logOutButton = view.findViewById(R.id.log_out)
        logOutButton.setOnClickListener {
            if(model.getCurrentUser().value?.isAnonymous == true) {
                Log.d("ProfileFrag", "You are guest")
                val intent = Intent(context, LoginActivity::class.java)
                startForResult.launch(intent)
            }
            else {
                Log.d("ProfileFrag", "You are not guest")
                model.signOut()
                model.guestUser()
            }
        }
        editButton.setOnClickListener {
            val intent = Intent(context, EditProfile::class.java)
            startForResult.launch(intent)
        }
    }
    private fun observeViewModel() {
        if (model.getCurrentUser().value?.isAnonymous == true){
            emailTextView.text = "Guest"
            userNameTextView.text = "Guest"
        }
        else{
            firebaseModel.getCurrentUser().observe(viewLifecycleOwner, Observer { user ->
                emailTextView.text = user.email
                userNameTextView.text = user.username
            })
        }
    }
}
