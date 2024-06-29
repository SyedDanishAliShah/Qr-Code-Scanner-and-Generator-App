package com.example.qrcode.activities.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qr_code_history")
data class QrCodeHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val result: String,
    val time: String,
    val date: String
)
