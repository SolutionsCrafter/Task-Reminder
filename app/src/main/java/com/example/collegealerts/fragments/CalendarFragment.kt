import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.collegealerts.R
import com.example.collegealerts.adapter.FilterdTaskAdapter
import com.example.collegealerts.data.DatabaseHelper
import com.example.collegealerts.data.Datas
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalendarFragment : Fragment() {

    private lateinit var selectedDate: String
    private lateinit var calendar: CalendarView
    private lateinit var recyclerView: RecyclerView
    private lateinit var filterdItemAdapter: FilterdTaskAdapter
    private var filterdData = mutableListOf<Datas>()
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var tvNoTasks: TextView
    private lateinit var imgNoTasks: ImageView
    private lateinit var btnEdit: ImageButton
    private val sampleData = mutableListOf<Datas>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d("CalendarFragment", "onCreateView started")

        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        // Initialize DatabaseHelper
        databaseHelper = DatabaseHelper(requireContext())
        Log.d("CalendarFragment", "DatabaseHelper initialized")

        tvNoTasks = view.findViewById(R.id.tvNoTasks)
        imgNoTasks = view.findViewById(R.id.imgNoTasks)
        calendar = view.findViewById(R.id.calendarView)

        val selectedDateMillis = calendar.date
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        selectedDate = sdf.format(Date(selectedDateMillis))
        Log.d("CalendarFragment", "Selected initial date: $selectedDate")

        view.findViewById<TextView>(R.id.tvTask).text = "${selectedDate}"

        // Setup RecyclerView
        recyclerView = view.findViewById(R.id.rvfilterd)
        recyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        Log.d("CalendarFragment", "RecyclerView set up")

        // Initialize adapter
        filterdItemAdapter = FilterdTaskAdapter(filterdData,
            onDeleteClick = { task ->
                deleteTask(task)  // Handle delete functionality
            },
            onAlertClick = { task ->
                setAlertForTask(task)  // Handle alert functionality
            },
            onDoneClick = { task ->
                doneTask(task)  // Handle done functionality
                //setAlertForTask(task)  // Handle alert functionality
            }
        )
        recyclerView.adapter = filterdItemAdapter
        Log.d("CalendarFragment", "Adapter initialized")

        loadTasksForDate(selectedDate)

        calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val cal = Calendar.getInstance()
            cal.set(year, month, dayOfMonth) // Set selected date
            this.selectedDate = sdf.format(cal.time) // Format properly
            Log.d("CalendarFragment", "Date changed: $selectedDate")
            view.findViewById<TextView>(R.id.tvTask).text = "${selectedDate}"
            // Load data from the database
            loadTasksForDate(selectedDate)
        }

        Log.d("CalendarFragment", "onCreateView completed")
        return view
    }

    private fun loadTasksForDate(date: String) {
        Log.d("CalendarFragment", "Loading tasks for date: $date")

        val tasks = databaseHelper.getTasksByDate(date)
        Log.d("CalendarFragment", "Number of tasks fetched: ${tasks.size}")

        // Clear the old list and add new tasks
        filterdData.clear()
        filterdData.addAll(tasks)

        // Log after updating the list of tasks
        Log.d("CalendarFragment", "Updated task list size: ${filterdData.size}")

        val allTasks = databaseHelper.getAllTasks()
        Log.d("CalendarFragment", "All tasks in DB: $allTasks")

        // If no tasks are available, show a Toast
        if (filterdData.isEmpty()) {
            recyclerView.visibility = View.GONE
            tvNoTasks.visibility = View.VISIBLE
            imgNoTasks.visibility = View.VISIBLE
        }else{
            tvNoTasks.visibility = View.GONE
            imgNoTasks.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            // Notify the adapter that the data has changed
            filterdItemAdapter.notifyDataSetChanged()
            Log.d("CalendarFragment", "RecyclerView adapter notified")
        }

    }

    // Handle delete functionality
    private fun deleteTask(task: Datas) {
        val deleted = databaseHelper.deleteTask(task)
        if (deleted > 0) {
            Toast.makeText(requireContext(), "Task deleted!", Toast.LENGTH_SHORT).show()
            loadTasksForDate(selectedDate) // Reload the tasks
        } else {
            Toast.makeText(requireContext(), "Failed to delete task", Toast.LENGTH_SHORT).show()
        }
    }

    // Handle done functionality
    private fun doneTask(task: Datas) {
        val updated = databaseHelper.doneTask(task)
        if (updated > 0) {
            Toast.makeText(requireContext(), "Task marked as done!", Toast.LENGTH_SHORT).show()
            loadTasksForDate(selectedDate) // Reload the tasks
        } else {
            Toast.makeText(requireContext(), "Failed to update task", Toast.LENGTH_SHORT).show()
        }
    }


    // Function to handle setting an alert for a task
    private fun setAlertForTask(task: Datas) {

    }

}
