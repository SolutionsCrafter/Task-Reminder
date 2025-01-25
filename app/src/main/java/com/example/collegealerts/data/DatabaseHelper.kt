package com.example.collegealerts.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.collegealerts.data.Datas

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "tasks.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_NAME = "tasks"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TASK_DATA = "task_data"
        private const val COLUMN_DATE_DATA = "date_data"
        private const val COLUMN_TIME_DATA = "time_data"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_TABLE_QUERY = ("CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_TASK_DATA TEXT," +
                "$COLUMN_DATE_DATA TEXT," +
                "$COLUMN_TIME_DATA TEXT)")
        db.execSQL(CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Add a new task to the database
    fun addTask(taskData: String, dateData: String, timeData: String) {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TASK_DATA, taskData)
        values.put(COLUMN_DATE_DATA, dateData)
        values.put(COLUMN_TIME_DATA, timeData)

        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    // Retrieve all tasks from the database
    fun getTasks(): List<Datas> {
        val db = readableDatabase
        val tasks = mutableListOf<Datas>()

        // Query the database
        val cursor: Cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_TASK_DATA, COLUMN_DATE_DATA, COLUMN_TIME_DATA),
            null, null, null, null, null
        )

        // Log the cursor column indices to ensure they are correct
        val columnIndexTaskData = cursor.getColumnIndex(COLUMN_TASK_DATA)
        val columnIndexDateData = cursor.getColumnIndex(COLUMN_DATE_DATA)
        val columnIndexTimeData = cursor.getColumnIndex(COLUMN_TIME_DATA)

        if (columnIndexTaskData == -1 || columnIndexDateData == -1 || columnIndexTimeData == -1) {
            Log.e("DatabaseHelper", "One or more columns are missing in the query result!")
            return tasks  // Return empty list or handle error accordingly
        }

        while (cursor.moveToNext()) {
            val taskData = cursor.getString(columnIndexTaskData)
            val dateData = cursor.getString(columnIndexDateData)
            val timeData = cursor.getString(columnIndexTimeData)

            val task = Datas(taskData, dateData, timeData)
            tasks.add(task)
        }

        cursor.close()
        return tasks
    }
}
