package com.example.qrcode.activities.db.entities.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.qrcode.activities.db.entities.QrCodeHistory

@Dao
interface QrCodeHistoryDao {

    @Insert
    suspend fun insert(qrCodeHistory: QrCodeHistory)

    @Query("SELECT * FROM qr_code_history")
    suspend fun getAllHistory(): List<QrCodeHistory>

    @Delete
    suspend fun delete(qrCodeHistory: QrCodeHistory)

}