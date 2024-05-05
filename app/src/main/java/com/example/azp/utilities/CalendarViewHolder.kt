package com.example.azp.utilities

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.azp.R

class CalendarViewHolder(
    itemView: View,
    private val onItemListener: OnItemListener
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    val dayOfMonth: TextView = itemView.findViewById(R.id.cellDayText)

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        // Проверяем, что дата не пустая
        if (dayOfMonth.text.isNotEmpty()) {
            // Вызываем метод onItemClick() из OnItemListener
            onItemListener.onItemClick(adapterPosition, dayOfMonth.text.toString())
        }
    }
}

