package com.example.qrcode.activities.db.entities.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.qrcode.activities.db.entities.QrCodeHistoryGenerated

@Dao
interface QrCodeHistoryGeneratedDao {

    @Insert
    suspend fun insert(qrCodeHistoryGenerated: QrCodeHistoryGenerated)

    @Query("SELECT * FROM qr_code_history_generated")
    suspend fun getAllHistory(): List<QrCodeHistoryGenerated>

    @Delete
    suspend fun delete(qrCodeHistoryGenerated: QrCodeHistoryGenerated)

}