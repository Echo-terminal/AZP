package com.example.azp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.azp.R
import com.example.azp.utilities.AuthRepository
import com.example.azp.utilities.AuthViewModel
import com.example.azp.utilities.AuthViewModelFactory

class LoginActivity: AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signupButton: Button
    private lateinit var guestButton: Button


    private val authRepository = AuthRepository()
    private val model: AuthViewModel by viewModels {
        AuthViewModelFactory(authRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        emailEditText = findViewById(R.id.edit_email_address)
        passwordEditText = findViewById(R.id.edit_password)
        loginButton = findViewById(R.id.log_in_btn)
        signupButton = findViewById(R.id.sign_up_btn)
        guestButton = findViewById(R.id.button_guest)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            model.signIn(email, password)

            val resultIntent = Intent()
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        signupButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            model.createUser(email, password)

            val resultIntent = Intent()
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

    }
}