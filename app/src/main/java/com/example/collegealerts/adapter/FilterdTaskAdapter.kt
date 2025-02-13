package com.example.collegealerts.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.collegealerts.R
import com.example.collegealerts.data.Datas

class FilterdTaskAdapter(
    private val itemList: MutableList<Datas>, // ✅ Changed to MutableList for item removal
    private val onDeleteClick: (Datas) -> Unit, // ✅ Lambda for delete functionality
    private val onAlertClick: (Datas) -> Unit  // ✅ Lambda for alert functionality
) : RecyclerView.Adapter<FilterdTaskAdapter.ItemViewHolder>() { // ✅ Fixed ViewHolder reference

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val task: TextView = itemView.findViewById(R.id.textView111)
        val date: TextView = itemView.findViewById(R.id.textView21)
        val time: TextView = itemView.findViewById(R.id.textView5)
        val alert: ImageView = itemView.findViewById(R.id.imgSetAlert)
        val delete: TextView = itemView.findViewById(R.id.tvDeleteTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task_filterd, parent, false)
        return ItemViewHolder(view) // ✅ Fixed ViewHolder instantiation
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = itemList[position]

        // ✅ Prevents crashes by handling null values
        holder.task.text = currentItem.taskData ?: "No Task"
        holder.date.text = currentItem.dateData ?: "No Date"
        holder.time.text = currentItem.timeData ?: "No Time"

        holder.alert.setOnClickListener {
            onAlertClick(currentItem)
        }

        holder.delete.setOnClickListener {
            showDeleteConfirmationDialog(holder.itemView.context, position)
        }
    }

    override fun getItemCount() = itemList.size

    private fun showDeleteConfirmationDialog(context: Context, position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Are you sure?")
        val currentItem = itemList[position]

        builder.setPositiveButton("Yes") { dialog, _ ->
            itemList.removeAt(position) // ✅ Remove from list
            notifyItemRemoved(position) // ✅ Notify RecyclerView
            onDeleteClick(currentItem)  // ✅ Notify parent activity
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }
}
