package com.example.azp.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.azp.R
import com.example.azp.adapter.FileAdapter
import com.example.azp.viewmodel.DocumentsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class DocumentsFragment : Fragment() {

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

        loadUserFiles()

        val searchEditText: EditText = view.findViewById(R.id.search_files)
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                fileAdapter.filter(s.toString())
            }
        })

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
}

