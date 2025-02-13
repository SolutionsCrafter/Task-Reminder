package com.example.collegealerts.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collegealerts.R
import com.example.collegealerts.adapter.ItemAdapter
import com.example.collegealerts.data.DatabaseHelper
import com.example.collegealerts.data.Datas

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: ItemAdapter
    private val sampleData = mutableListOf<Datas>()
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize DatabaseHelper
        databaseHelper = DatabaseHelper(requireContext())

        // Setup RecyclerView
        recyclerView = view.findViewById(R.id.rvTasks)
        recyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        // Initialize adapter
        itemAdapter = ItemAdapter(sampleData,
            onDeleteClick = { task ->
                deleteTask(task)  // Handle delete functionality
            },
            onAlertClick = { task ->
                setAlertForTask(task)  // Handle alert functionality
            }
        )
        recyclerView.adapter = itemAdapter

        // Load data from the database
        loadDataFromDatabase()

        return view
    }

    //TODO edit tasks

    // Function to load data from database
    private fun loadDataFromDatabase() {
        val tasks = databaseHelper.getAllTasks() // Fetch tasks from database
        sampleData.clear() // Clear old data
        sampleData.addAll(tasks) // Add new data to the list
        itemAdapter.notifyDataSetChanged() // Notify adapter about the change
    }

    // Function to add new data if needed
    private fun addNewData(newData: Datas) {
        sampleData.add(newData)
        itemAdapter.notifyItemInserted(sampleData.size - 1)
    }

    // Handle delete functionality
    private fun deleteTask(task: Datas) {
        val deleted = databaseHelper.deleteTask(task) // Call the delete function from DatabaseHelper
        if (deleted > 0) {
            sampleData.remove(task) // Remove from the list
            itemAdapter.notifyDataSetChanged() // Notify the adapter
            Toast.makeText(requireContext(), "Task deleted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Failed to delete task", Toast.LENGTH_SHORT).show()
        }
    }


    // Function to handle setting an alert for a task
    private fun setAlertForTask(task: Datas) {

    }



}
