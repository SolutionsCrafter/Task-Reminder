package com.example.collegealerts.data

import android.app.DownloadManager.COLUMN_ID
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * DatabaseHelper class for managing SQLite database operations.
 * This class provides methods for creating, inserting, retrieving, updating, and deleting tasks.
 *
 * @param context The application context.
 */
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "CollegeAlerts.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "tasks"
        private const val COMPLETED_TABLE_NAME = "completed_tasks"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TASK = "taskData"
        private const val COLUMN_DATE = "dateData"
        private const val COLUMN_TIME = "timeData"
        private const val TASK_SUMMARY_TABLE = "summary"
    }

    /**
     * Called when the database is first created.
     * Creates a table to store tasks.
     *
     * @param db The database instance.
     */
    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TASK TEXT,
                $COLUMN_DATE TEXT,
                $COLUMN_TIME TEXT
            );
        """.trimIndent()

        val createSummaryTable = """
            CREATE TABLE $TASK_SUMMARY_TABLE (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                upcoming_tasks INTEGER DEFAULT 0,
                today_tasks INTEGER DEFAULT 0,
                completed_tasks INTEGER DEFAULT 0,
                missed_tasks INTEGER DEFAULT 0
            );
        """.trimIndent()

        db.execSQL(createTable)
        db.execSQL(createSummaryTable)

        // Insert an initial row in task_summary with default values
        db.execSQL("INSERT INTO $TASK_SUMMARY_TABLE (upcoming_tasks, today_tasks, completed_tasks, missed_tasks) VALUES (0, 0, 0, 0)")
    }
    /**
     * Called when the database needs to be upgraded.
     * This implementation drops the existing table and recreates it.
     *
     * @param db The database instance.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        db.execSQL("DROP TABLE IF EXISTS $COMPLETED_TABLE_NAME")
        onCreate(db)
    }

    /**
     * Retrieves all tasks from the database.
     *
     * @return A list of [Datas] objects containing task information.
     */
    fun getAllTasks(): List<Datas> {
        val taskList = mutableListOf<Datas>()
        val db = readableDatabase
        val query = "SELECT $COLUMN_TASK, $COLUMN_DATE, $COLUMN_TIME FROM $TABLE_NAME ORDER BY $COLUMN_DATE ASC, $COLUMN_TIME ASC"
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


    /**
     * Inserts a new task into the database.
     *
     * @param taskData The task description.
     * @param dateData The task date.
     * @param timeData The task time.
     * @return The row ID of the newly inserted row, or -1 if an error occurred.
     */
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

    /**
     * Deletes a task from the database.
     *
     * @param task The task object to delete.
     * @return The number of rows deleted.
     */
    fun deleteTask(task: Datas): Int {
        val db = writableDatabase
        val todayDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()) // Get today's date

        // Find task category before deleting
        val query = "SELECT $COLUMN_DATE FROM $TABLE_NAME WHERE $COLUMN_TASK = ?"
        val cursor = db.rawQuery(query, arrayOf(task.taskData))

        var taskDate: String? = null
        if (cursor.moveToFirst()) {
            taskDate = cursor.getString(0) // Get task date
        }
        cursor.close()

        // Delete task
        val selection = "$COLUMN_TASK = ?"
        val selectionArgs = arrayOf(task.taskData)
        val rowsDeleted = db.delete(TABLE_NAME, selection, selectionArgs)

        if (rowsDeleted > 0 && taskDate != null) {
            // Update the relevant task count
            when {
                taskDate == todayDate -> db.execSQL("UPDATE $TASK_SUMMARY_TABLE SET today_tasks = today_tasks - 1 WHERE today_tasks > 0")
                taskDate < todayDate -> db.execSQL("UPDATE $TASK_SUMMARY_TABLE SET missed_tasks = missed_tasks - 1 WHERE missed_tasks > 0")
                taskDate > todayDate -> db.execSQL("UPDATE $TASK_SUMMARY_TABLE SET upcoming_tasks = upcoming_tasks - 1 WHERE upcoming_tasks > 0")
            }
        }

        Log.d("DatabaseHelper", "Rows deleted: $rowsDeleted")
        db.close()
        return rowsDeleted
    }


    fun doneTask(task: Datas): Int {
        val db = writableDatabase
        val todayDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        // Find task category before deleting
        val query = "SELECT $COLUMN_DATE FROM $TABLE_NAME WHERE $COLUMN_TASK = ?"
        val cursor = db.rawQuery(query, arrayOf(task.taskData))

        var taskDate: String? = null
        if (cursor.moveToFirst()) {
            taskDate = cursor.getString(0) // Get task date
        }
        cursor.close()

        // Delete task
        val selection = "$COLUMN_TASK = ?"
        val selectionArgs = arrayOf(task.taskData)
        val rowsDeleted = db.delete(TABLE_NAME, selection, selectionArgs)

        if (rowsDeleted > 0 && taskDate != null) {
            // Increment completed_tasks
            db.execSQL("UPDATE $TASK_SUMMARY_TABLE SET completed_tasks = completed_tasks + 1")

            // Update the relevant task count
            when {
                taskDate == todayDate -> db.execSQL("UPDATE $TASK_SUMMARY_TABLE SET today_tasks = today_tasks - 1 WHERE today_tasks > 0")
                taskDate < todayDate -> db.execSQL("UPDATE $TASK_SUMMARY_TABLE SET missed_tasks = missed_tasks - 1 WHERE missed_tasks > 0")
                taskDate > todayDate -> db.execSQL("UPDATE $TASK_SUMMARY_TABLE SET upcoming_tasks = upcoming_tasks - 1 WHERE upcoming_tasks > 0")
            }
        }

        Log.d("DatabaseHelper", "Task marked as done: $rowsDeleted")
        db.close()
        return rowsDeleted
    }



    /**
     * Retrieves tasks from the database that match a specific date.
     *
     * @param date The date for which tasks are to be fetched.
     * @return A list of [Datas] objects containing the tasks for the specified date.
     */
    fun getTasksByDate(date: String): List<Datas> {
        val taskList = mutableListOf<Datas>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_DATE = ? ORDER BY $COLUMN_DATE ASC, $COLUMN_TIME ASC"
        val cursor = db.rawQuery(query, arrayOf(date))

        if (cursor.moveToFirst()) {
            do {
                val task = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK))
                val taskDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                val taskTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME))
                taskList.add(Datas(task, taskDate, taskTime))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return taskList
    }

    /**
     * Updates an existing task in the database.
     *
     * @param id The task ID to update (not currently used in the query).
     * @param newTask The new task description.
     * @param newDate The new task date.
     * @param newTime The new task time.
     * @return `true` if the update was successful, `false` otherwise.
     */
    fun updateTask(id: Int, newTask: String, newDate: String, newTime: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_DATE, newDate)
            put(COLUMN_TIME, newTime)
        }

        // Update the time and date based on the task name
        val result = db.update(
            TABLE_NAME,
            contentValues,
            "$COLUMN_TASK = ?",
            arrayOf(newTask) // Use task name as the condition
        )
        db.close()

        Log.d("DatabaseHelper", "Update result: $result") // Log the result to see if it is > 0
        return result > 0
    }

    fun getTodayTaskCount(): Int {
        val db = readableDatabase
        val todayDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()) // Get today's date

        val query = "SELECT COUNT(*) FROM $TABLE_NAME WHERE $COLUMN_DATE = ?"
        val cursor = db.rawQuery(query, arrayOf(todayDate))

        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0) // Get the count from the first column
        }

        cursor.close()
        db.close()
        return count
    }

    fun getPastTaskCount(): Int {
        val db = readableDatabase
        val todayDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()) // Get today's date

        val query = "SELECT COUNT(*) FROM $TABLE_NAME WHERE $COLUMN_DATE < ?"
        val cursor = db.rawQuery(query, arrayOf(todayDate))

        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0) // Get the count from the first column
        }

        cursor.close()
        db.close()
        return count
    }


    fun getUpcomingTaskCount(): Int {
        val db = readableDatabase
        val todayDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()) // Get today's date

        val query = "SELECT COUNT(*) FROM $TABLE_NAME WHERE $COLUMN_DATE > ?"
        val cursor = db.rawQuery(query, arrayOf(todayDate))

        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }

        cursor.close()
        db.close()
        return count
    }

    fun getCompletedTasks(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT completed_tasks FROM $TASK_SUMMARY_TABLE", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return count
    }

}
