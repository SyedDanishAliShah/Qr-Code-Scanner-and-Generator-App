package com.example.qrcode.activities.db.entities

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.qrcode.activities.db.entities.dao.QrCodeHistoryDao
import com.example.qrcode.activities.db.entities.dao.QrCodeHistoryGeneratedDao

@Database(entities = [QrCodeHistory::class, QrCodeHistoryGenerated::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun qrCodeHistoryDao(): QrCodeHistoryDao
    abstract fun qrCodeHistoryGeneratedDao() : QrCodeHistoryGeneratedDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "qr_code_history"
                    )
                        // Add migration from version 1 to version 2
                        .addMigrations(MIGRATION_1_2)
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Perform the migration from version 1 to version 2
                // For example, you can create a new table, copy data, etc.
                db.execSQL("ALTER TABLE QrCodeHistory ADD COLUMN new_column_name TEXT")
            }
        }
    }
}