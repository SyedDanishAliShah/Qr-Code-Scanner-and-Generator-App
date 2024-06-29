package com.example.qrcode.activities

import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.room.Room
import com.example.qrcode.R
import com.example.qrcode.activities.db.entities.AppDatabase
import com.example.qrcode.activities.db.entities.QrCodeHistoryGenerated
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class GeneratedQrCodeResultActivity : AppCompatActivity() {

    private lateinit var generatedQrCodeImage: ImageView
    private lateinit var backIcon: ImageView
    private lateinit var generateAgainButton: ImageView
    private lateinit var homeButton: ImageView
    private lateinit var typeInputText: TextView
    private lateinit var textPassedFromLastActivity: TextView
    private lateinit var downloadImageView: ImageView
    private lateinit var shareImageView: ImageView
    private lateinit var appDatabase: AppDatabase
    private lateinit var decorateImageView: ImageView
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private var progressDialog: ProgressDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_generate_result)

        MobileAds.initialize(this) {
            // Displaying a log that AdMob ads have been initialized.
            Log.i("Admob", "Admob Initialized.")
        }

        val adView: AdView = findViewById(R.id.adView_activity_qr_generate_result)
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container_qr_code_generate_result)

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

        generatedQrCodeImage = findViewById(R.id.generated_qr_code_image)
        backIcon = findViewById(R.id.back_icon_5)
        generateAgainButton = findViewById(R.id.generate_again_rectangle_for_generate_result_screen)
        homeButton = findViewById(R.id.home_rectangle_for_generate_result_screen)
        typeInputText = findViewById(R.id.type_input_tv)
        textPassedFromLastActivity = findViewById(R.id.message_passed_from_last_screen)
        downloadImageView = findViewById(R.id.circle_for_share_and_download_logo_download_generate)
        shareImageView = findViewById(R.id.circle_for_share_and_download_logo_share_generate)
        decorateImageView = findViewById(R.id.circle_for_share_and_download_logo_for_decorate_generate)


        appDatabase = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "qr_code_history")
                .build()


        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        generateAgainButton.setOnClickListener {
            val intent = Intent(this, QrGeneratorActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Get the bitmap from the intent
        val bitmap = intent.getParcelableExtra<Bitmap>("QR_CODE_BITMAP")

        downloadImageView.setOnClickListener {
            if (bitmap != null) {
                saveImageToGallery(bitmap)
            }
        }


        shareImageView.setOnClickListener {
            // Get the bitmap from the intent
            val bitmap = intent.getParcelableExtra<Bitmap>("QR_CODE_BITMAP")

            // Save the bitmap to a file
            val imageUri = bitmap?.let { it1 -> saveBitmapToFile(it1) }

            // Share the image
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            try {
                startActivity(Intent.createChooser(shareIntent, "Share via"))
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(this, "No suitable app found", Toast.LENGTH_SHORT).show()
            }
        }

        // Get the selected type and text from the intent
        val selectedType = intent.getStringExtra("selectedType")
        val enteredText = intent.getStringExtra("enteredText")

        typeInputText.text = selectedType
        textPassedFromLastActivity.text = enteredText

        generatedQrCodeImage.setImageBitmap(bitmap)

        backIcon.setOnClickListener {
            val intent = Intent(this, QrGeneratorActivity::class.java)
            startActivity(intent)
            finish()
        }
        val time = intent.getStringExtra("time")
        val timeTv = findViewById<TextView>(R.id.time_tv_generate)
        timeTv.text = time

        val date = intent.getStringExtra("date")
        val dateTv = findViewById<TextView>(R.id.date_tv_generate)
        dateTv.text = date

        val qrCodeHistory = QrCodeHistoryGenerated(
            text = enteredText.toString(),
            time = time.toString(),
            date = date.toString()
        )
        insertData(qrCodeHistory)

        decorateImageView.setOnClickListener {
            showProgressDialog() // Show the progress dialog before loading the ad

            MobileAds.initialize(this@GeneratedQrCodeResultActivity) {
                // on below line displaying a log that admob ads has been initialized.
                Log.i("Admob", "Admob Initialized.")
            }
            // on below line creating and initializing variable for adRequest
            val adRequestInterstitialAd = AdRequest.Builder().build()
            InterstitialAd.load(
                this@GeneratedQrCodeResultActivity,
                "ca-app-pub-3940256099942544/1033173712",
                adRequestInterstitialAd,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        val intent = Intent(
                            this@GeneratedQrCodeResultActivity,
                            DecorateQrCodeActivity::class.java
                        )
                        intent.putExtra("QR_CODE_BITMAP", bitmap) // Pass the bitmap extra
                        intent.putExtra("time", time)
                        intent.putExtra("date", date)
                        intent.putExtra("selectedType", selectedType)
                        intent.putExtra("enteredText", enteredText)
                        startActivity(intent)
                        finish()
                        // this method is called when ad is loaded in that case we are displaying our ad.
                        interstitialAd.fullScreenContentCallback =
                            object : FullScreenContentCallback() {
                                override fun onAdDismissedFullScreenContent() {

                                }

                                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                                    // Called when fullscreen content failed to show.
                                    // Dismiss the progress dialog
                                    dismissProgressDialog()
                                }

                                override fun onAdShowedFullScreenContent() {
                                    // Called when fullscreen content is shown.
                                    // Dismiss the progress dialog
                                    dismissProgressDialog()
                                    // Set the fullscreen content callback to null to avoid memory leaks.
                                    interstitialAd.fullScreenContentCallback = null
                                }
                            }
                        interstitialAd.show(this@GeneratedQrCodeResultActivity)

                    }


                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        // this method is called when we get any error
                        Toast.makeText(
                            this@GeneratedQrCodeResultActivity,
                            "Fail to load ad..",
                            Toast.LENGTH_SHORT
                        ).show()
                        dismissProgressDialog() // Dismiss the progress dialog if ad loading fails
                    }

                })

        }

    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun insertData(qrCodeHistoryGenerated: QrCodeHistoryGenerated) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                appDatabase.qrCodeHistoryGeneratedDao().insert(qrCodeHistoryGenerated)
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


    private fun saveBitmapToFile(bitmap: Bitmap): Uri? {
        val imagesDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "images")
        imagesDir.mkdirs() // Make sure the directory exists
        val imageFile = File(imagesDir, "QR_Code_${System.currentTimeMillis()}.jpg")

        try {
            FileOutputStream(imageFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
            return FileProvider.getUriForFile(this, "${packageName}.fileprovider", imageFile)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
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
    private fun showProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog?.setMessage("Please wait, loading ad...")
        progressDialog?.setCancelable(false)
        progressDialog?.show()
    }

    private fun dismissProgressDialog() {
        progressDialog?.dismiss()
    }
}