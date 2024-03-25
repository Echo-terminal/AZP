package com.example.azp.activities

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.azp.AuthViewModel
import com.example.azp.R

class LoginActivity: AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signupButton: Button

    private val model: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        emailEditText = findViewById(R.id.edit_email_address)
        passwordEditText = findViewById(R.id.edit_password)
        loginButton = findViewById(R.id.log_in_btn)
        signupButton = findViewById(R.id.sign_up_btn)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            model.signInWithEmailAndPassword(email, password)

            finish()
        }

        signupButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            model.createUserWithEmailAndPassword(email, password)

            finish()
        }

    }
}