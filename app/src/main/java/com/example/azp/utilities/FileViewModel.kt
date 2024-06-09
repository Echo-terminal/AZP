package com.example.azp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class DocumentsViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _fileList = MutableLiveData<MutableList<String>>()
    val fileList: MutableLiveData<MutableList<String>> get() = _fileList


    init {
        loadFilesFromFirestore()
    }

    private fun loadFilesFromFirestore() {
        db.collection("files")
            .get()
            .addOnSuccessListener { result ->
                val files = mutableListOf<String>()
                for (document in result) {
                    val fileName = document.getString("fileName")
                    if (fileName != null) {
                        files.add(fileName)
                    }
                }
                _fileList.postValue(files) // Используем postValue() для обновления LiveData
            }
            .addOnFailureListener { exception ->
                // Handle any errors
            }
    }

    fun addFile(fileName: String) {
        val file = hashMapOf("fileName" to fileName)
        db.collection("files")
            .add(file)
            .addOnSuccessListener { documentReference ->
                _fileList.value?.add(fileName)
                _fileList.postValue(_fileList.value) // Используем postValue() для обновления LiveData
            }
            .addOnFailureListener { e ->
                // Handle any errors
            }
    }
}
