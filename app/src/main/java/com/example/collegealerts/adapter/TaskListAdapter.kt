package com.example.collegealerts.adapter
import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.collegealerts.R
import com.example.collegealerts.data.DatabaseHelper
import com.example.collegealerts.data.Datas
import com.example.collegealerts.edit_task

const val REQUEST_CODE_EDIT = 100

class ItemAdapter(
    private val itemList: List<Datas>,
    private val onDeleteClick: (Datas) -> Unit, // Lambda for delete functionality
    private val onAlertClick: (Datas) -> Unit  // Lambda for alert functionality (optional)
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //val imageView: ImageView = itemView.findViewById(R.id.imageView21)
        val task: TextView = itemView.findViewById(R.id.tvTaskTitle)
        val date: TextView = itemView.findViewById(R.id.tvDate)
        val time: TextView = itemView.findViewById(R.id.tvTime)
        val alert: ImageButton = itemView.findViewById(R.id.btnAlert)
        val delete: ImageButton = itemView.findViewById(R.id.btnDelete)
        val edit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val done: CheckBox = itemView.findViewById(R.id.checkboxComplete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = itemList[position]
        //holder.imageView.setImageResource(currentItem.index)
        holder.task.text = currentItem.taskData
        holder.date.text = currentItem.dateData
        holder.time.text = currentItem.timeData

        holder.alert.setOnClickListener {

            onAlertClick(currentItem)
        }

        holder.delete.setOnClickListener {
            showDeleteConfirmationDialog(holder.itemView.context, position)
        }

        holder.edit.setOnClickListener {
            val intent = Intent(holder.itemView.context, edit_task::class.java)
            intent.putExtra("TASK", currentItem.taskData)
            intent.putExtra("DATE", currentItem.dateData)
            intent.putExtra("TIME", currentItem.timeData)
            intent.putExtra("POSITION", position) // Pass position instead of ID

            (holder.itemView.context as Activity).startActivityForResult(intent, REQUEST_CODE_EDIT)
        }

        holder.done.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showDeleteConfirmationDialog(holder.itemView.context, position) {
                    //holder.done.setButtonDrawable(R.drawable.check_box_on) // Checked drawable

                    // Get the correct task data
                    val task = itemList[position]

                    // Initialize database helper
                    val dbHelper = DatabaseHelper(holder.itemView.context)

                    // Add the task to completed_tasks table
                    onDeleteClick(currentItem)
                }
            } else {
                holder.done.setButtonDrawable(R.drawable.check_box_off) // Unchecked drawable
            }
        }




    }

    override fun getItemCount() = itemList.size

    private fun showDeleteConfirmationDialog(context: Context, position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Are you sure")
        val currentItem = itemList[position]

        builder.setPositiveButton("Yes") { dialog, _ ->
            onDeleteClick(currentItem)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun showDeleteConfirmationDialog(context: Context, position: Int, onConfirm: () -> Unit) {
        AlertDialog.Builder(context)
            .setTitle("Confirm Delete")
            .setMessage("Are you sure?")
            .setPositiveButton("Yes") { _, _ ->
                onConfirm() // Execute the callback if user confirms
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


}