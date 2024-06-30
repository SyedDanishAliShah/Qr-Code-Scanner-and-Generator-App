package com.example.qrcode.activities

import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.qrcode.R
import com.example.qrcode.activities.db.entities.AppDatabase
import com.example.qrcode.activities.db.entities.QrCodeHistory
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class QrCodeScanResultActivity  : AppCompatActivity() {

    private lateinit var backIcon : ImageView
    private lateinit var shareImageView: ImageView
    private lateinit var downloadImageView: ImageView
    private lateinit var homeButton : ImageView
    private lateinit var scanAgainButton : ImageView
    private lateinit var capturedBitmap: Bitmap
    private lateinit var appDatabase: AppDatabase
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_scan_result)

        MobileAds.initialize(this) {
            // Displaying a log that AdMob ads have been initialized.
            Log.i("Admob", "Admob Initialized.")
        }

        val adView: AdView = findViewById(R.id.adView_activity_qr_code_scan_result)
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container_qr_code_scan_result)

        val adRequest: AdRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        shimmerFrameLayout.startShimmer()
        shimmerFrameLayout.visibility = View.VISIBLE

        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                shimmerFrameLayout.hideShimmer()

            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                super.onAdFailedToLoad(adError)
                // Ad failed to load, handle this situation
                Log.e("Admob", "Ad failed to load: ${adError.message}")
            }
        }

        capturedBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

        // Retrieve the image file path from intent extras
        val imagePath = intent.getStringExtra("imagePath")

        // Load the bitmap from the file path
        if (!imagePath.isNullOrEmpty()) {
            val imageFile = File(imagePath)
            capturedBitmap = BitmapFactory.decodeFile(imageFile.absolutePath)

            appDatabase = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "qr_code_history").build()
        }

        shareImageView = findViewById(R.id.share_icon_scan_result_screen)
        downloadImageView = findViewById(R.id.circle_for_share_and_download_logo_for_download)
        homeButton = findViewById(R.id.home_rectangle_for_scanner_result_screen)
        scanAgainButton = findViewById(R.id.scan_again_rectangle_for_scan_result_screen)

        scanAgainButton.setOnClickListener {
            finish()
        }

        homeButton.setOnClickListener {
            finish()
        }


            downloadImageView.setOnClickListener {
                saveImageToGallery(capturedBitmap)
            }


        val result = intent.getStringExtra("result")
        val webAddressTv1 = findViewById<TextView>(R.id.web_address_tv_1)
        webAddressTv1.text = result

        val time = intent.getStringExtra("time")
        val timeTv = findViewById<TextView>(R.id.time_tv)
        timeTv.text = time

        val date = intent.getStringExtra("date")
        val dateTv = findViewById<TextView>(R.id.date_tv)
        dateTv.text = date

        // Insert data into Room database
        val qrCodeHistory = QrCodeHistory(result = result.toString(), time = time.toString(), date = date.toString())
        insertData(qrCodeHistory)

        shareImageView.setOnClickListener {
            val webAddress = webAddressTv1.text.toString()

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, webAddress)

            try {
                startActivity(Intent.createChooser(shareIntent, "Share via"))
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(this, "No suitable app found", Toast.LENGTH_SHORT).show()
            }
        }

        backIcon = findViewById(R.id.back_icon_1)

        backIcon.setOnClickListener {
            finish()
        }
          }
    @OptIn(DelicateCoroutinesApi::class)
    private fun insertData(qrCodeHistory: QrCodeHistory) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                appDatabase.qrCodeHistoryDao().insert(qrCodeHistory)
                showToast("Data inserted successfully")
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Failed to insert data")
            }
        }
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveImageToGallery(bitmap: Bitmap) {
        // Define the content values for the image file
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "QR_Code_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        // Insert the image file into the media store
        val contentResolver = applicationContext.contentResolver
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let { imageUri ->
            try {
                // Open an output stream to write the bitmap data
                contentResolver.openOutputStream(imageUri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }

                // If Android version is Q or higher, mark image as non-pending after writing
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    contentResolver.update(imageUri, contentValues, null, null)
                }

                // Notify user that image has been saved
                Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                e.printStackTrace()
                // Notify user of any error during saving
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
            }
        }
    }


}