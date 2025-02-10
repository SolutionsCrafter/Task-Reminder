package com.example.collegealerts.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "CollegeAlerts.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "tasks"
        private const val COLUMN_TASK = "taskData"
        private const val COLUMN_DATE = "dateData"
        private const val COLUMN_TIME = "timeData"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TASK TEXT,
                $COLUMN_DATE TEXT,
                $COLUMN_TIME TEXT
            )
        """
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Method to fetch all tasks
    fun getAllTasks(): List<Datas> {
        val taskList = mutableListOf<Datas>()
        val db = readableDatabase
        val query = "SELECT $COLUMN_TASK, $COLUMN_DATE, $COLUMN_TIME FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val task = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                val time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME))
                taskList.add(Datas(task, date, time))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return taskList
    }

    // Method to insert a new task
    fun insertTask(taskData: String, dateData: String, timeData: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TASK, taskData)
            put(COLUMN_DATE, dateData)
            put(COLUMN_TIME, timeData)
        }

        val result = db.insert(TABLE_NAME, null, values)
        db.close()
        return result
    }

    // Method to delete task by taskData
    fun deleteTask(task: Datas): Int {
        val db = writableDatabase
        val selection = "$COLUMN_TASK = ?"
        val selectionArgs = arrayOf(task.taskData)  // Using the task's data for deletion
        val rowsDeleted = db.delete(TABLE_NAME, selection, selectionArgs)
        db.close()
        return rowsDeleted
    }

}
