package com.example.collegealerts

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
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
import com.example.collegealerts.data.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class addTask : AppCompatActivity() {

    lateinit var btnCalendar: ImageView
    lateinit var tvDate: TextView
    lateinit var btnTime: ImageView
    lateinit var tvTime: TextView
    lateinit var btnSave: Button
    private lateinit var databaseHelper: DatabaseHelper

    var task: String = ""
    var date: String = ""
    var time: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_task)

        // Initialize the database
        databaseHelper = DatabaseHelper(this)

        btnCalendar = findViewById(R.id.imgBtnCalendar)
        tvDate = findViewById(R.id.tvDate)
        btnTime = findViewById(R.id.imgBtnTime)
        tvTime = findViewById(R.id.tvTime)
        val etTask = findViewById<EditText>(R.id.edTask)

        pickDate()
        pickTime()

        btnSave = findViewById(R.id.btnSave)
        btnSave.setOnClickListener {
            task = etTask.text.toString()
            date = tvDate.text.toString()
            time = tvTime.text.toString()

            if (task.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty()) {
                saveToDatabase(task, date, time)
                etTask.text.clear()
                tvDate.text = ""
                tvTime.text = ""
                Toast.makeText(this, "Task added to database!", Toast.LENGTH_SHORT).show()
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

    private fun pickDate() {
        btnCalendar.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    // ✅ Ensures two-digit day and month format (e.g., 05-08-2024)
                    val formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                    tvDate.text = formattedDate
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }
    }


    private fun pickTime() {
        btnTime.setOnClickListener {
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    val selectedTime = Calendar.getInstance()
                    selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    selectedTime.set(Calendar.MINUTE, minute)

                    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    tvTime.text = timeFormat.format(selectedTime.time)
                },
                hour,
                minute,
                false
            )
            timePickerDialog.show()
        }
    }

    private fun saveToDatabase(task: String, date: String, time: String) {
        val rowId = databaseHelper.insertTask(task, date, time)
        if (rowId != -1L) {
            Log.d("addTask", "Task saved to database: $task, $date, $time")

            // Convert the date and time into a Calendar object
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

            try {
                // Set the date and time from the selected task details
                calendar.time = dateFormat.parse(date) ?: Calendar.getInstance().time
                val timeParts = time.split(" ")
                val timeSplit = timeParts[0].split(":")
                calendar.set(Calendar.HOUR_OF_DAY, timeSplit[0].toInt())
                calendar.set(Calendar.MINUTE, timeSplit[1].toInt())

                // Schedule the notification
                scheduleNotification(task, time, calendar.timeInMillis)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // Navigate back to MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Failed to save task", Toast.LENGTH_SHORT).show()
        }
    }


    private fun scheduleNotification(task: String, time: String, triggerTime: Long) {
        val intent = Intent(this, NotificationReceiver::class.java)
        intent.putExtra("TASK", task)
        intent.putExtra("TIME", time)

        // Create a PendingIntent
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        // Get the AlarmManager system service
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Schedule the alarm
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)

        Toast.makeText(this, "Reminder set for $task at $time", Toast.LENGTH_SHORT).show()
    }



}
