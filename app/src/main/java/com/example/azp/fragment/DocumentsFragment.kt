package com.example.azp.fragment

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.azp.R
import com.example.azp.adapter.FileAdapter
import com.example.azp.utilities.AuthViewModel
import com.example.azp.utilities.AuthViewModelFactory
import com.example.azp.viewmodel.DocumentsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class DocumentsFragment : Fragment() {

    private val PICK_FILE_REQUEST = 1
    private lateinit var storageReference: StorageReference
    private lateinit var fileAdapter: FileAdapter
    private lateinit var documentsViewModel: DocumentsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_documents, container, false)

        storageReference = FirebaseStorage.getInstance().reference
        documentsViewModel = ViewModelProvider(requireActivity()).get(DocumentsViewModel::class.java)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewFiles)
        recyclerView.layoutManager = LinearLayoutManager(context)
        fileAdapter = FileAdapter(documentsViewModel.fileList.value ?: mutableListOf(), requireContext()) // передаем контекст в адаптер
        recyclerView.adapter = fileAdapter

        documentsViewModel.fileList.observe(viewLifecycleOwner, { files ->
            fileAdapter.updateFiles(files)
        })

        val uploadButton: Button = view.findViewById(R.id.uploadButton)
        uploadButton.setOnClickListener {
            openFileChooser()
        }

        loadUserFiles()

        return view
    }

    private fun loadUserFiles() {
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid ?: return

        val db = FirebaseFirestore.getInstance()
        db.collection("files")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val files = documents.map { it.getString("fileName") ?: "Unknown file" }.toMutableList()
                documentsViewModel.fileList.postValue(files)
            }
            .addOnFailureListener { exception ->
                Log.w("DocumentsFragment", "Error getting files: ", exception)
            }
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, PICK_FILE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val fileUri = data.data!!
            uploadFile(fileUri)
        } else {
            Log.e("DocumentsFragment", "No file selected or activity result not OK")
        }
    }

    private fun uploadFile(fileUri: Uri) {
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid ?: return

        val fileReference = storageReference.child("uploads/$userId/${fileUri.lastPathSegment}")
        Log.d("DocumentsFragment", "Uploading to: ${fileReference.path}")

        fileReference.putFile(fileUri)
            .addOnSuccessListener {
                Toast.makeText(context, "Upload successful", Toast.LENGTH_SHORT).show()
                fileReference.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    Log.d("DocumentsFragment", "File available at: $downloadUrl")
                    Toast.makeText(context, "File available at: $downloadUrl", Toast.LENGTH_LONG).show()
                    saveFileMetadataToFirestore(userId, fileUri.lastPathSegment ?: "Unknown file", downloadUrl)
                }.addOnFailureListener { exception ->
                    Log.e("DocumentsFragment", "Failed to get download URL", exception)
                    Toast.makeText(context, "Failed to get download URL: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("DocumentsFragment", "Upload failed", exception)
                Toast.makeText(context, "Upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
            .addOnProgressListener { taskSnapshot ->
                val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                Log.d("DocumentsFragment", "Upload is $progress% done")
            }
    }

    private fun saveFileMetadataToFirestore(userId: String, fileName: String, downloadUrl: String) {
        val db = FirebaseFirestore.getInstance()
        val fileData = hashMapOf(
            "userId" to userId,
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
