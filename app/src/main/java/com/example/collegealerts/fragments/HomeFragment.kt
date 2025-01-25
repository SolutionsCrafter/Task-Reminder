package com.example.collegealerts.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collegealerts.MainActivity
import com.example.collegealerts.R
import com.example.collegealerts.adapter.ItemAdapter
import com.example.collegealerts.data.Datas


class HomeFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: ItemAdapter
    private val sampleData = mutableListOf<Datas>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)


        // Retrieve the passed arguments
        val taskTitle = arguments?.getString("task_title")
        val taskDate = arguments?.getString("task_date")
        val taskTime = arguments?.getString("task_time")

        // Create a Datas object and add to list if not null
        if (taskTitle != null && taskDate != null && taskTime != null) {
            val newData = Datas(taskTitle, taskDate, taskTime)
            sampleData.add(newData)

        }

        Log.d("HomeFragment", "Received task: $taskTitle, Date: $taskDate, Time: $taskTime")


        // Setup RecyclerView
        recyclerView = view.findViewById(R.id.rvTasks)
        recyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        // Initialize adapter
        itemAdapter = ItemAdapter(sampleData)
        recyclerView.adapter = itemAdapter

        return view
    }

    // Function to add new data if needed
    //fun addNewData(newData: Datas) {
        //sampleData.add(newData)
        //itemAdapter.notifyItemInserted(sampleData.size - 1)
    //}

}