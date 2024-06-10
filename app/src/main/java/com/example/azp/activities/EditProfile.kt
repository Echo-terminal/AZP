package com.example.azp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.azp.R
import com.example.azp.data_classes.User
import com.example.azp.utilities.AuthViewModel
import com.example.azp.utilities.AuthViewModelFactory
import com.example.azp.utilities.TaskViewModel
import com.example.azp.utilities.AuthRepository
import com.example.azp.utilities.TaskFirebaseRepository
import com.example.azp.utilities.TaskViewModelFactory
import com.example.azp.utilities.UID
import com.google.gson.Gson

class EditProfile  : AppCompatActivity() {
    private val firebaseModel: TaskViewModel by viewModels {
        TaskViewModelFactory(TaskFirebaseRepository())
    }
    private val authModel: AuthViewModel by viewModels {
        AuthViewModelFactory(AuthRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        var userEmail: String = ""

        val editEmail = findViewById<TextView>(R.id.edit_profile_email)
        val editUsername = findViewById<TextView>(R.id.edit_profile_username)

        firebaseModel.getCurrentUser().observe(this, Observer { user ->
            userEmail = user.email
            editEmail.text = user.email
            editUsername.text = user.username
        })


        val saveButton = findViewById<Button>(R.id.save_button)

        saveButton.setOnClickListener {
            val newEmail = editEmail.text.toString()
            val newUsername = editUsername.text.toString()

            val newUser = User(UID, newUsername, newEmail)

            val resultIntent = Intent()
            val gson = Gson()
            val json = gson.toJson(newUser)
            resultIntent.putExtra("json", json)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }
}