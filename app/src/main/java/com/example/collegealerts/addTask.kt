package com.example.collegealerts

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class addTask : AppCompatActivity() {

    lateinit var btnCalendar:ImageView
    lateinit var tvDate:TextView
    lateinit var btnTime:ImageView
    lateinit var tvTime:TextView
    lateinit var btnSave:Button

    var task:String = ""
    var date:String = ""
    var time:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_task)

        btnCalendar = findViewById(R.id.imgBtnCalendar)
        tvDate = findViewById(R.id.tvDate)
        btnTime= findViewById(R.id.imgBtnTime)
        tvTime = findViewById(R.id.tvTime)
        val etTask = findViewById<EditText>(R.id.edTask)

        //task = etTask.text.toString()
        pickDate()
        pickTime()

        btnSave = findViewById(R.id.btnSave)
        btnSave.setOnClickListener {
            task = etTask.text.toString()
            date = tvDate.text.toString()
            time = tvTime.text.toString()

            if (task.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty()) {
                passData()
                etTask.text.clear()
                tvDate.text = ""
                tvTime.text = ""
                Toast.makeText(this, "Data added successfully!", Toast.LENGTH_SHORT).show()
            } else {

                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun pickDate(){
        btnCalendar.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    tvDate.text =
                        (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                    date = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }
    }

    private fun pickTime(){
        btnTime.setOnClickListener {
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(
                this,
                { view, hourOfDay, minute ->
                    tvTime.setText("$hourOfDay:$minute")
                    val selectedTime = Calendar.getInstance()

                    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    val formattedTime = timeFormat.format(selectedTime.time)

                    tvTime.text = formattedTime
                    time = formattedTime
                },
                hour,
                minute,
                false
            )
            timePickerDialog.show()
        }
    }

    private fun passData() {

        // Check the data before passing it
        Log.d("addTask", "Task: $task, Date: $date, Time: $time")

        val intent = Intent(this, MainActivity::class.java)

        // Pass data with the intent
        intent.putExtra("task_title", task)
        intent.putExtra("task_date", date)
        intent.putExtra("task_time", time)

        // Start the HomeActivity
        startActivity(intent)

        // Set the bundle as arguments to the fragment
        //homeFragment.arguments = bundle

        // Replace or add the fragment
        //supportFragmentManager.beginTransaction()
            //.replace(R.id.frag_view, homeFragment) // Make sure 'fragment_container' is correct
            //.addToBackStack(null) // Add to back stack if you want to navigate back
            //.commit()

        // Log that the data has been passed
        Log.d("addTask", "Data passed to fragment.")
    }

}