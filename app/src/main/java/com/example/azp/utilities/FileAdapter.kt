package com.example.azp.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.azp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class FileAdapter(
    private var files: MutableList<String>,
    private val context: Context
) : RecyclerView.Adapter<FileAdapter.FileViewHolder>() {

    private var allFiles = ArrayList(files)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val fileName = files[position]
        holder.fileNameTextView.text = fileName

        holder.itemView.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            val userId = user?.uid ?: return@setOnClickListener
            val storageReference = FirebaseStorage.getInstance().reference
            val fileReference = storageReference.child("uploads/$userId/$fileName")
            fileReference.downloadUrl.addOnSuccessListener { uri ->
                val downloadUrl = uri.toString()
                downloadFile(fileName, downloadUrl)
            }.addOnFailureListener { exception ->
                Log.e("FileAdapter", "Failed to get download URL", exception)
                Toast.makeText(context, "Failed to get download URL: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return files.size
    }

    fun updateFiles(newFiles: MutableList<String>) {
        files = newFiles
        allFiles = ArrayList(newFiles)
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        files = if (query.isEmpty()) {
            allFiles
        } else {
            allFiles.filter { it.contains(query, true) }.toMutableList()
        }
        notifyDataSetChanged()
    }

    private fun downloadFile(fileName: String, downloadUrl: String) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as android.app.DownloadManager
        val uri = android.net.Uri.parse(downloadUrl)
        val request = android.app.DownloadManager.Request(uri)
        request.setTitle(fileName)
        request.setDescription("Downloading file...")
        request.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(android.os.Environment.DIRECTORY_DOWNLOADS, fileName)
        downloadManager.enqueue(request)
        Toast.makeText(context, "Downloading $fileName", Toast.LENGTH_SHORT).show()
    }

    class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fileNameTextView: TextView = itemView.findViewById(R.id.fileNameTextView)
    }
}

