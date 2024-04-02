package com.example.azp.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.azp.R

class ListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        val view = inflater.inflate(R.layout.fragment_list, container, false)

        val b_in_progress = view.findViewById<Button>(R.id.button4)
        val b_to_do = view.findViewById<Button>(R.id.button5)
        val b_milestones = view.findViewById<Button>(R.id.button6)
        val b_complete = view.findViewById<Button>(R.id.button7)

        b_in_progress.setOnClickListener{
            Toast.makeText(requireContext(), "b_in_progress clicked", Toast.LENGTH_SHORT).show()
        }

        b_to_do.setOnClickListener{
            Toast.makeText(requireContext(), "b_to_do clicked", Toast.LENGTH_SHORT).show()
        }

        b_milestones.setOnClickListener{
            Toast.makeText(requireContext(), "b_milestones clicked", Toast.LENGTH_SHORT).show()
        }

        b_complete.setOnClickListener{
            Toast.makeText(requireContext(), "b_complete clicked", Toast.LENGTH_SHORT).show()
        }





        //val addButton = view.findViewById<Button>(R.id.addButton1)

        var addItemList = mutableListOf(
            Task("Follow AndroidDevs")
        )

        val addButton1 = view.findViewById<ImageButton>(R.id.addButton1)
        //val textView_title = view.findViewById<EditText>(R.id.textView_title)
        val recyclerView_1 = view.findViewById<RecyclerView>(R.id.recyclerView_1)

        val adapter = TaskAdapter(addItemList)
        recyclerView_1.adapter = adapter
        recyclerView_1.layoutManager = LinearLayoutManager(requireActivity())

        /*addButton1.setOnClickListener {
            //val title = textView_title.text.toString()
            //val task = Task(title)
            //addItemList.add(task)
            adapter.notifyItemInserted(addItemList.size - 1)

        }*/

        addButton1.setOnClickListener{

        }

        //важно, всегда в конце
        return view
    }

}