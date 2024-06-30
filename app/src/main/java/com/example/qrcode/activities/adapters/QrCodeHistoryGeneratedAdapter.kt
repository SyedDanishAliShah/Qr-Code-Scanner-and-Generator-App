package com.example.qrcode.activities.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.qrcode.R
import com.example.qrcode.activities.db.entities.AppDatabase
import com.example.qrcode.activities.db.entities.QrCodeHistoryGenerated
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class QrCodeHistoryGeneratedAdapter(private var qrCodeHistoryList: List<QrCodeHistoryGenerated>, private val appDatabase: AppDatabase) :
    RecyclerView.Adapter<QrCodeHistoryGeneratedAdapter.ViewHolder>() {

    fun setData(qrCodeHistoryList: List<QrCodeHistoryGenerated>) {
        this.qrCodeHistoryList = qrCodeHistoryList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.items_history, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: QrCodeHistoryGeneratedAdapter.ViewHolder, position: Int) {
        val qrCodeHistory = qrCodeHistoryList[position]
        holder.bind(qrCodeHistory)
    }

    override fun getItemCount(): Int {
        return qrCodeHistoryList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val resultTextView: TextView = itemView.findViewById(R.id.qr_data_tv)
        private val timeTextView: TextView = itemView.findViewById(R.id.time_tv_1)
        private val dateTextView: TextView = itemView.findViewById(R.id.date_tv_1)

        fun bind(qrCodeHistory: QrCodeHistoryGenerated) {
            resultTextView.text = qrCodeHistory.text
            timeTextView.text = qrCodeHistory.time
            dateTextView.text = qrCodeHistory.date
        }

        init {
            // Add a long click listener to the item view
            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    showDeleteDialog(position)
                }
                true
            }
        }

        private fun showDeleteDialog(position: Int) {
            val context = itemView.context
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete Item")
            builder.setMessage("Are you sure you want to delete this item?")
            builder.setPositiveButton("Yes") { _, _ ->
                deleteItem(position)
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }

        private fun deleteItem(position: Int) {
            val qrCodeHistory = qrCodeHistoryList[position]
            qrCodeHistoryList = qrCodeHistoryList.filterIndexed { index, _ -> index != position }
            notifyDataSetChanged() // Notify adapter of item removal
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    appDatabase.qrCodeHistoryGeneratedDao().delete(qrCodeHistory)
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Handle error deleting item from database
                }
            }
        }
    }
}