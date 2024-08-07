package com.example.qrcode.activities.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qrcode.R
import com.example.qrcode.activities.adapters.QrCodeHistoryAdapter
import com.example.qrcode.activities.db.entities.AppDatabase
import com.example.qrcode.activities.db.entities.QrCodeHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ReadQrCodeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: QrCodeHistoryAdapter
    private lateinit var appDatabase: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_read_qr_code, container, false)

        recyclerView = view.findViewById(R.id.recycler_view_fragment_read_qr_code)
        recyclerView.layoutManager = LinearLayoutManager(context)

        appDatabase = AppDatabase.getInstance(requireContext())

        adapter = QrCodeHistoryAdapter(emptyList(), appDatabase) // Initialize adapter with empty list
        recyclerView.adapter = adapter

        fetchDataFromDatabase()

        return view
    }

    private fun fetchDataFromDatabase() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val qrCodeHistoryList = appDatabase.qrCodeHistoryDao().getAllHistory()
                updateUI(qrCodeHistoryList)
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle error fetching data from database
            }
        }
    }

    private fun updateUI(qrCodeHistoryList: List<QrCodeHistory>) {
        GlobalScope.launch(Dispatchers.Main) {
            adapter.setData(qrCodeHistoryList.toMutableList()) // Pass a mutable copy of the list
        }
    }
}