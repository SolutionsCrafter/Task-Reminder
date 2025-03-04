package com.example.collegealerts

import android.app.Activity
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
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.collegealerts.data.DatabaseHelper
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class edit_task : AppCompatActivity() {

    lateinit var btnCalendar: TextInputLayout
    lateinit var tvDate: TextView
    lateinit var btnTime: TextInputLayout
    lateinit var tvTime: TextView
    lateinit var btnSave: Button
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_task)

        // Initialize the database
        databaseHelper = DatabaseHelper(this)

        val task = intent.getStringExtra("TASK")
        val date = intent.getStringExtra("DATE")
        val time = intent.getStringExtra("TIME")
        val position = intent.getIntExtra("POSITION", -1)


        var newTask:String
        var newDate:String
        var newTime:String

        newTask = task.toString()
        newDate = date.toString()
        newTime = time.toString()

        //btnCalendar = findViewById(R.id.imgBtnCalendar)
        tvDate = findViewById(R.id.tvDate)
        //btnTime = findViewById(R.id.imgBtnTime)
        tvTime = findViewById(R.id.tvTime)
        val etTask = findViewById<TextView>(R.id.edTask)

        etTask.text = task.toString()
        tvDate.hint = date.toString()
        tvTime.hint = time.toString()

        pickDate()
        pickTime()

        btnSave = findViewById(R.id.btnSave)
        btnSave.setOnClickListener {
            newDate = if (tvDate.text.isEmpty()) date.toString() else tvDate.text.toString()
            newTime = if (tvTime.text.isEmpty()) time.toString() else tvTime.text.toString()

//            if (newDate.isEmpty() || newTime.isEmpty()) {
//                Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }

            if (position != -1) {
                val isUpdated = databaseHelper.updateTask(position, newTask, newDate, newTime)
                if (isUpdated) {
                    Toast.makeText(this, "Task Updated!", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK) // Notify previous activity
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Update Failed!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Invalid Task!", Toast.LENGTH_SHORT).show()
            }
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun pickDate() {
        tvDate.setOnClickListener {
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
        tvTime.setOnClickListener {
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


}
