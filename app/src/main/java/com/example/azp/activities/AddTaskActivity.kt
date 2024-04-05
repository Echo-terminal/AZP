package com.example.azp.activities

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.azp.R

class AddTaskActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_add_task)

        val cancelBtn = findViewById<Button>(R.id.button_Cancel)
        cancelBtn.setOnClickListener {
            finish()
        }

        val saveBtn = findViewById<Button>(R.id.button_Save)
        val editTitle = findViewById<TextView>(R.id.editTextTitle)
        val editDate = findViewById<TextView>(R.id.editTextDate)
        val stateToDo = findViewById<CheckBox>(R.id.checkBox2_to_do)

    }
}