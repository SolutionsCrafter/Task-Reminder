package com.example.collegealerts.adapter

import android.app.AlertDialog
import android.content.ClipData
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.collegealerts.R
import com.example.collegealerts.data.Datas

class ItemAdapter(
    private val itemList: List<Datas>,
    private val onDeleteClick: (Datas) -> Unit, // Lambda for delete functionality
    private val onAlertClick: (Datas) -> Unit  // Lambda for alert functionality (optional)
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //val imageView: ImageView = itemView.findViewById(R.id.imageView21)
        val task: TextView = itemView.findViewById(R.id.textView111)
        val date: TextView = itemView.findViewById(R.id.textView21)
        val time: TextView = itemView.findViewById(R.id.textView5)
        val alert: ImageView = itemView.findViewById(R.id.imgSetAlert)
        val delete: TextView = itemView.findViewById(R.id.tvDeleteTask)
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

}