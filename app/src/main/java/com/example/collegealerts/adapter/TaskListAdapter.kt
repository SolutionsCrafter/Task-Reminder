package com.example.collegealerts.adapter
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.collegealerts.R
import com.example.collegealerts.data.DatabaseHelper
import com.example.collegealerts.data.Datas
import com.example.collegealerts.edit_task
import com.saadahmedev.popupdialog.PopupDialog
import com.saadahmedev.popupdialog.PopupDialog.*
import com.saadahmedev.popupdialog.listener.StandardDialogActionListener

const val REQUEST_CODE_EDIT = 100

class ItemAdapter(
    private val itemList: List<Datas>,
    private val onDeleteClick: (Datas) -> Unit, // Lambda for delete functionality
    private val onAlertClick: (Datas) -> Unit, // Lambda for alert functionality (optional)
    private val onDoneClick: (Datas) -> Unit  // Lambda for done functionality (optional)
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
                showDoneConfirmationDialog(holder.itemView.context, position) {
                }
            } else {
                holder.done.setButtonDrawable(R.drawable.check_box_off) // Unchecked drawable
            }
        }


    }

    override fun getItemCount() = itemList.size

    //Delete task confirmation
    private fun showDeleteConfirmationDialog(context: Context, position: Int) {
        val currentItem = itemList[position] // Get the item to delete

        getInstance(context)
            .standardDialogBuilder()
            .createStandardDialog()
            .setHeading("Are you sure?")
            .setDescription("Do you really want to delete this item? This action cannot be undone.")
            .setIcon(R.drawable.icon_delete) // Use a delete icon
            .setIconColor(R.color.popup_icon) // Warning color for delete action
            .setPositiveButtonText("Yes")
            .setNegativeButtonText("Cancel")
            .setPositiveButtonTextColor(R.color.white)
            .setNegativeButtonTextColor(R.color.popup_button_text)
            .setPositiveButtonBackgroundColor(R.color.primary_color) // Red delete button
            .setNegativeButtonBackgroundColor(R.color.popup_button_negative) // Gray cancel button
            .build(object : StandardDialogActionListener {
                override fun onPositiveButtonClicked(dialog: Dialog) {
                    onDeleteClick(currentItem) // Execute delete action
                    dialog.dismiss()
                }

                override fun onNegativeButtonClicked(dialog: Dialog) {
                    dialog.dismiss()
                }
            })
            .show()
    }

    //Done task confirmation
    private fun showDoneConfirmationDialog(context: Context, position: Int, onConfirm: () -> Unit) {
        val currentItem = itemList[position] // Get the item to delete
        PopupDialog.getInstance(context)
            .standardDialogBuilder()
            .createStandardDialog()
            .setHeading("Confirm Task Completion")
            .setDescription("Are you sure you want to mark this task as completed? Once confirmed, it will be removed from the list.")
            .setIcon(R.drawable.icon_done) // Use an appropriate delete icon
            .setIconColor(R.color.popup_button_positive)
            .setPositiveButtonText("Yes")
            .setNegativeButtonText("No")
            .setPositiveButtonTextColor(R.color.white)
            .setNegativeButtonTextColor(R.color.popup_button_text)
            .setPositiveButtonBackgroundColor(R.color.popup_button_positive) // Red delete button
            .setNegativeButtonBackgroundColor(R.color.popup_button_negative) // Gray cancel button
            .build(object : StandardDialogActionListener {
                override fun onPositiveButtonClicked(dialog: Dialog) {
                    onConfirm() // Execute delete action
                    onDoneClick(currentItem)
                    dialog.dismiss()
                }

                override fun onNegativeButtonClicked(dialog: Dialog) {
                    dialog.dismiss()
                }
            })
            .show()
    }



}