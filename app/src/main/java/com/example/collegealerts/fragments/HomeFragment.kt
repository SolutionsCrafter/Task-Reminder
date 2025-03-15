package com.example.collegealerts.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collegealerts.R
import com.example.collegealerts.adapter.ItemAdapter
import com.example.collegealerts.addTask
import com.example.collegealerts.data.DatabaseHelper
import com.example.collegealerts.data.Datas

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var gridRecyclerView: RecyclerView
    private lateinit var itemAdapter: ItemAdapter
    private val sampleData = mutableListOf<Datas>()
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var addTaskBtn: Button
    private lateinit var tvGridUpcoming: TextView
    private lateinit var tvGridToday: TextView
    private lateinit var tvGridComplete: TextView
    private lateinit var tvGridMissed: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize DatabaseHelper
        databaseHelper = DatabaseHelper(requireContext())

        tvGridUpcoming = view.findViewById<TextView>(R.id.tvGridUpcoming)
        tvGridToday = view.findViewById<TextView>(R.id.tvGridToday)
        tvGridComplete = view.findViewById<TextView>(R.id.tvGridComplete)
        tvGridMissed = view.findViewById<TextView>(R.id.tvGridMissed)

        tvGridUpcoming.text = "0"
        tvGridToday.text = "0"
        tvGridComplete.text = "0"
        tvGridMissed.text = "0"

        tvGridUpcoming.text = databaseHelper.getUpcomingTaskCount().toString()
        tvGridToday.text = databaseHelper.getTodayTaskCount().toString()
        tvGridComplete.text = databaseHelper.getCompletedTasks().toString()
        tvGridMissed.text = databaseHelper.getPastTaskCount().toString()

        addTaskBtn = view.findViewById<Button>(R.id.btnAddTask)
        addTaskBtn.setOnClickListener {
            val intent = Intent(requireContext(), addTask::class.java)
            startActivity(intent)
        }

        // Setup Task RecyclerView
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
            },
            onDoneClick = { task ->
                doneTask(task)  // Handle done functionality
            },
        )
        recyclerView.adapter = itemAdapter

        // Load data from the database
        loadDataFromDatabase()

        return view
    }

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
        val deleted = databaseHelper.deleteTask(task)
        if (deleted > 0) {
            sampleData.remove(task)
            itemAdapter.notifyDataSetChanged()
            Toast.makeText(requireContext(), "Task deleted!", Toast.LENGTH_SHORT).show()

            refreshTaskCounts() // Refresh UI after deletion
        } else {
            Toast.makeText(requireContext(), "Failed to delete task", Toast.LENGTH_SHORT).show()
        }
    }


    // Handle done functionality
    private fun doneTask(task: Datas) {
        val deleted = databaseHelper.doneTask(task)
        if (deleted > 0) {
            sampleData.remove(task)
            itemAdapter.notifyDataSetChanged()
            Toast.makeText(requireContext(), "Task marked as done!", Toast.LENGTH_SHORT).show()

            refreshTaskCounts() // Refresh UI after marking as done
        } else {
            Toast.makeText(requireContext(), "Failed to update task", Toast.LENGTH_SHORT).show()
        }
    }

    private fun refreshTaskCounts() {
        tvGridUpcoming.text = databaseHelper.getUpcomingTaskCount().toString()
        tvGridToday.text = databaseHelper.getTodayTaskCount().toString()
        tvGridComplete.text = databaseHelper.getCompletedTasks().toString()
        tvGridMissed.text = databaseHelper.getPastTaskCount().toString()

        loadDataFromDatabase() // Refresh task list
    }

    // Function to handle setting an alert for a task
    private fun setAlertForTask(task: Datas) {

    }

}
