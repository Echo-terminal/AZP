package com.example.azp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.azp.R

class FileAdapter(
    private var files: MutableList<String>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<FileAdapter.FileViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(fileName: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file, parent, false)
        return FileViewHolder(view)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val fileName = files[position]
        holder.fileNameTextView.text = fileName

        holder.itemView.setOnClickListener {
            listener.onItemClick(fileName)
        }
    }

    override fun getItemCount(): Int {
        return files.size
    }

    fun updateFiles(newFiles: MutableList<String>) {
        files = newFiles
        notifyDataSetChanged()
    }

    class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fileNameTextView: TextView = itemView.findViewById(R.id.fileNameTextView)
    }
}
