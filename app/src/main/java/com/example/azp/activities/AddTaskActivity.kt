package com.example.azp.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import com.example.azp.R
import com.example.azp.adapter.FileAdapter
import com.example.azp.data_classes.Date
import com.example.azp.data_classes.Task
import com.example.azp.data_classes.TaskState
import com.example.azp.utilities.TaskFirebaseRepository
import com.example.azp.utilities.TaskViewModel
import com.example.azp.utilities.TaskViewModelFactory
import com.example.azp.viewmodel.DocumentsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AddTaskActivity : AppCompatActivity() {

    private val PICK_FILE_REQUEST = 1
    private lateinit var storageReference: StorageReference
    private lateinit var documentsViewModel: DocumentsViewModel

    private lateinit var taskDateTextView: TextView
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var taskDate: Date
    private lateinit var dateCompleted: Date
    private var selectedState:TaskState = TaskState.NONE
    private var fileURL: Uri? = null

    private var key: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)
        taskDate = Date.now()
        dateCompleted = Date(1,1,1)
        storageReference = FirebaseStorage.getInstance().reference
        documentsViewModel = ViewModelProvider(this).get(DocumentsViewModel::class.java)

        initDatePicker()
        taskDateTextView = findViewById(R.id.currentDate)

        taskDateTextView.text = taskDate.toString()
        taskDateTextView.setOnClickListener {
            openDatePicker()
        }

        val cancelBtn = findViewById<Button>(R.id.button_Cancel)
        cancelBtn.setOnClickListener {
            finish()
        }

        val addBtn = findViewById<Button>(R.id.button_Add_File)
        val saveBtn = findViewById<Button>(R.id.button_Save)
        val editTitle = findViewById<TextView>(R.id.editTextTitle)
        val editTextDescription = findViewById<TextView>(R.id.editTextDescription) //поле описания
        val stateToDo = findViewById<CheckBox>(R.id.checkBox2_to_do)
        val stateInProcess = findViewById<CheckBox>(R.id.checkBox1_in_progress)
        val stateComplete = findViewById<CheckBox>(R.id.checkBox4_complete)
        val stateMilestone = findViewById<CheckBox>(R.id.checkBox3_milestones)

                stateToDo.setOnCheckedChangeListener { _ , isChecked ->
            if (isChecked) {
                selectedState = TaskState.TODO

                listOf(stateMilestone, stateInProcess, stateComplete)
                    .forEach { it.isChecked = false }
            }
        }
        stateInProcess.setOnCheckedChangeListener { _ , isChecked ->
            if (isChecked) {
                selectedState = TaskState.IN_PROGRESS

                listOf(stateToDo, stateMilestone, stateComplete)
                    .forEach { it.isChecked = false }
            }
        }
        stateComplete.setOnCheckedChangeListener { _ , isChecked ->
            if (isChecked) {
                selectedState = TaskState.COMPLETED

                listOf(stateToDo, stateInProcess, stateMilestone)
                    .forEach { it.isChecked = false }
            }
        }
        stateMilestone.setOnCheckedChangeListener { _ , isChecked ->
            if (isChecked) {
                selectedState = TaskState.MILESTONE

                listOf(stateToDo, stateInProcess, stateComplete)
                    .forEach { it.isChecked = false }
            }
        }

        saveBtn.setOnClickListener {
            key = FirebaseDatabase.getInstance().getReference().push().key
            val taskTitle = editTitle.text.toString()
            val taskDescription = editTextDescription.text.toString()
            val newTask = Task(key!!, taskTitle, taskDescription, selectedState, taskDate, dateCompleted)
            if (fileURL!=null) {
                uploadFile(fileURL!!)
            }
            if (newTask.getState()==TaskState.COMPLETED) newTask.setDateCom(Date.now())
            val resultIntent = Intent()
            val gson = Gson()
            val json = gson.toJson(newTask)
            resultIntent.putExtra("json", json)
            if(taskTitle != "" && selectedState != TaskState.NONE){
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Empty task", Toast.LENGTH_SHORT).show()
            }
        }

        addBtn.setOnClickListener {
            openFileChooser()
        }
    }





    private fun initDatePicker() {
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, day: Int ->
                val adjustedMonth = month + 1
                taskDate = Date(year, adjustedMonth, day) // Создаем объект Date
                taskDateTextView.text = taskDate.toString() // Устанавливаем выбранную дату на кнопку
            }

        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val style = AlertDialog.THEME_HOLO_LIGHT

        datePickerDialog = DatePickerDialog(this, style, dateSetListener, year, month, day)
    }

    private fun openDatePicker() {
        datePickerDialog.show()
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, PICK_FILE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            fileURL = data.data!!
        } else {
            Log.e("DocumentsFragment", "No file selected or activity result not OK")
        }
    }

    private fun uploadFile(fileUri: Uri) {
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid ?: return

        val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(java.util.Date())
        // Получение расширения файла
        val extension = fileUri.lastPathSegment?.substringAfterLast('.', "") ?: ""

        // Формирование имени файла с использованием текущей даты и времени и расширения файла
        val fileName = if (extension.isNotEmpty()) {
            "$timestamp.$extension"
        } else {
            timestamp
        }

        val fileReference = storageReference.child("uploads/$userId/$fileName")
        Log.d("DocumentsFragment", "Uploading to: ${fileReference.path}")

        fileReference.putFile(fileUri)
            .addOnSuccessListener {
                Toast.makeText(this, "Upload successful", Toast.LENGTH_SHORT).show()
                fileReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    Log.d("DocumentsFragment", "File available at: $downloadUrl")
                    Toast.makeText(this, "File available at: $downloadUrl", Toast.LENGTH_LONG).show()
                    saveFileMetadataToFirestore(userId, fileName, downloadUrl, key!!)
                }.addOnFailureListener { exception ->
                    Log.e("DocumentsFragment", "Failed to get download URL", exception)
                    Toast.makeText(this, "Failed to get download URL: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("DocumentsFragment", "Upload failed", exception)
                Toast.makeText(this, "Upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
            .addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                Log.d("DocumentsFragment", "Upload is $progress% done")
            }
    }

    private fun saveFileMetadataToFirestore(userId: String, fileName: String, downloadUrl: String, taskId: String) {
        val db = FirebaseFirestore.getInstance()
        val fileData = hashMapOf(
            "userId" to userId,
            "taskId" to taskId,
            "fileName" to fileName,
            "downloadUrl" to downloadUrl
        )

        db.collection("files")
            .add(fileData)
            .addOnSuccessListener {
                Log.d("DocumentsFragment", "File metadata saved successfully")
                documentsViewModel.addFile(fileName)
            }
            .addOnFailureListener { e ->
                Log.w("DocumentsFragment", "Error adding file metadata", e)
            }
    }

}