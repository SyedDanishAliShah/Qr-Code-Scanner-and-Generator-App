package com.example.qrcode.activities


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.qrcode.R
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class QrCodeScreenActivity : AppCompatActivity() {

    private lateinit var backIcon: ImageView
    private lateinit var qrCodeTakePhotoCard: ImageView
    private lateinit var qrCodeChooseFromGalleryCard : ImageView
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImageUri: Uri? = result.data?.data
            // Decode the QR code from the selected image URI
            selectedImageUri?.let { uri ->
                val inputStream = contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                // Save the bitmap to a file
                val imagePath = saveBitmapToFile(bitmap)

                // Prepare intent to navigate to QrCodeScanResultActivity
                val intent = Intent(this@QrCodeScreenActivity, QrCodeScanResultActivity::class.java)
                // Pass the decoded web address and the URI of the selected image to QrCodeScanResultActivity
                intent.putExtra("result", decodeQrCode(bitmap)) // Assuming you have a function to decode QR code
                intent.putExtra("imagePath", imagePath)
                val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                intent.putExtra("time", currentTime)
                val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                intent.putExtra("date", currentDate)
                // Start the activity
                startActivity(intent)
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_scan)

        MobileAds.initialize(this) {
            // Displaying a log that AdMob ads have been initialized.
            Log.i("Admob", "Admob Initialized.")
        }

        val adView: AdView = findViewById(R.id.adView_activity_qr_code_scan)
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container_qr_code_scan)

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

        backIcon = findViewById(R.id.back_icon)
        qrCodeTakePhotoCard = findViewById(R.id.qr_code_screen_camera_gallery_card_for_photo)
        qrCodeChooseFromGalleryCard = findViewById(R.id.qr_code_screen_camera_gallery_card_for_gallery)

        backIcon.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        qrCodeTakePhotoCard.setOnClickListener {
            val intent = Intent(this, CameraOpenActivity::class.java)
            startActivity(intent)
            finish()
        }

        qrCodeChooseFromGalleryCard.setOnClickListener {
            openGallery()
        }

    }

    private fun saveBitmapToFile(bitmap: Bitmap): String {
        val imageFileName = "QR_Code_${System.currentTimeMillis()}.jpg"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(storageDir, imageFileName)

        return try {
            val outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            imageFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle error
            ""
        }
    }

    private fun decodeQrCode(bitmap: Bitmap): String? {
        val intArray = IntArray(bitmap.width * bitmap.height)
        // Copy pixel data from the Bitmap into the 'intArray'
        bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        val source = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
        val reader = MultiFormatReader()

        return try {
            val result = reader.decode(binaryBitmap)
            result.text // This is the decoded text from the QR code
        } catch (e: Exception) {
            e.printStackTrace()
            null // Return null if decoding fails
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this , MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}