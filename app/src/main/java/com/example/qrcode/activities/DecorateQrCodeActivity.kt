package com.example.qrcode.activities

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.qrcode.R
import com.example.qrcode.activities.fragments.BottomSheetFragment
import com.example.qrcode.activities.fragments.BottomSheetFragmentQrDesign
import com.example.qrcode.activities.fragments.BottomSheetFragmentText
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds

class DecorateQrCodeActivity : AppCompatActivity() {

    private lateinit var backIcon : ImageView
    private lateinit var qrCodeImageView: ImageView
    private lateinit var logoImageView: ImageView
    private lateinit var originalQrCodeBitmap: Bitmap // Keep a reference to the original QR code image bitmap
    private lateinit var applyIcon : ImageView
    private lateinit var qrDesignImageView: ImageView
    private lateinit var textImageView: ImageView
    private lateinit var shimmerFrameLayout : ShimmerFrameLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decorate_qr_code)

        MobileAds.initialize(this) {
            // Displaying a log that AdMob ads have been initialized.
            Log.i("Admob", "Admob Initialized.")
        }

        val adView: AdView = findViewById(R.id.adView_activity_decorate_qr)
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container_decorate_qr)

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

        backIcon = findViewById(R.id.back_icon_6)
        qrCodeImageView = findViewById(R.id.generated_qr_code_image_1)
        logoImageView = findViewById(R.id.logo_icon_decorate_screen)
        applyIcon = findViewById(R.id.apply_icon_decorate_screen)
        qrDesignImageView = findViewById(R.id.qr_color_icon_decorate_screen)
        textImageView = findViewById(R.id.text_logo_decorate_screen)

        textImageView.setOnClickListener {
            val bottomSheetFragmentText = BottomSheetFragmentText()
            bottomSheetFragmentText.show(supportFragmentManager, bottomSheetFragmentText.tag)
        }

        qrDesignImageView.setOnClickListener {
            val bottomSheetFragmentQrDesign = BottomSheetFragmentQrDesign()
            bottomSheetFragmentQrDesign.show(supportFragmentManager, bottomSheetFragmentQrDesign.tag)
        }

        applyIcon.setOnClickListener {
            val combinedBitmap = (qrCodeImageView.drawable as BitmapDrawable).bitmap
            saveImageToGallery(combinedBitmap)
        }

        logoImageView.setOnClickListener {
            val bottomSheetFragment = BottomSheetFragment()
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }

        // Get the bitmap extra from the intent
        val bitmap = intent.getParcelableExtra<Bitmap>("QR_CODE_BITMAP")
        if (bitmap != null) { originalQrCodeBitmap = bitmap
        } // Save the original QR code image bitmap
        val selectedType = intent.getStringExtra("selectedType")
        val enteredText = intent.getStringExtra("enteredText")
        val time = intent.getStringExtra("time")
        val date = intent.getStringExtra("date")

        // Set the bitmap to the qrCodeImageView
        qrCodeImageView.setImageBitmap(bitmap)

        backIcon.setOnClickListener {
            val intent = Intent(this, GeneratedQrCodeResultActivity::class.java)
            intent.putExtra("QR_CODE_BITMAP", bitmap) // Pass the bitmap back
            intent.putExtra("time", time)
            intent.putExtra("date", date)
            intent.putExtra("selectedType", selectedType)
            intent.putExtra("enteredText", enteredText)
            startActivity(intent)
            finish()
        }

        }

    fun applySelectedImage(imageResId: Int) {
        val bitmap = BitmapFactory.decodeResource(resources, imageResId)
        val drawable = qrCodeImageView.drawable
        if (drawable is BitmapDrawable) {
            val qrBitmap = drawable.bitmap

            val combinedBitmap = combineBitmaps(qrBitmap, bitmap)

            qrCodeImageView.setImageBitmap(combinedBitmap)
        } else {
            // Handle the case when the drawable is not a BitmapDrawable
        }
    }

    fun applySelectedColor(drawableResId: Int) {
        val qrBitmap = originalQrCodeBitmap.copy(originalQrCodeBitmap.config, true) // Make a copy of the original bitmap

        val width = qrBitmap.width
        val height = qrBitmap.height
        val combinedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(combinedBitmap)
        val paint = Paint()

        val color = ContextCompat.getColor(this, drawableResId) // Get the color associated with the drawableResId

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixelColor = qrBitmap.getPixel(x, y)
                if (pixelColor == Color.BLACK) {
                    paint.color = color
                    canvas.drawPoint(x.toFloat(), y.toFloat(), paint)
                } else {
                    paint.color = pixelColor
                    canvas.drawPoint(x.toFloat(), y.toFloat(), paint)
                }
            }
        }

        qrCodeImageView.setImageBitmap(combinedBitmap)
    }

    fun applySelectedText(text: String) {
        val qrBitmap = originalQrCodeBitmap.copy(originalQrCodeBitmap.config, true) // Make a copy of the original bitmap

        val width = qrBitmap.width
        val height = qrBitmap.height
        val combinedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(combinedBitmap)

        // Draw the original QR code bitmap
        canvas.drawBitmap(qrBitmap, 0f, 0f, null)

        // Calculate the position to draw the text (above the black part of the QR code)
        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 30f // Adjust the text size as needed
        }
        val textWidth = textPaint.measureText(text)
        val x = (width - textWidth) / 2 // Center the text horizontally
        val y = height * 0.1f // Position the text 10% from the top of the image

        // Draw the text
        canvas.drawText(text, x, y, textPaint)

        qrCodeImageView.setImageBitmap(combinedBitmap)
    }
    // Clear the selected image from the bottom sheet dialog
    fun clearSelectedImage() {
        qrCodeImageView.setImageBitmap(originalQrCodeBitmap)
    }

    private fun combineBitmaps(qrBitmap: Bitmap, selectedBitmap: Bitmap): Bitmap {
        val maxWidth = qrBitmap.width.coerceAtLeast(selectedBitmap.width)
        val maxHeight = qrBitmap.height.coerceAtLeast(selectedBitmap.height)

        val resultBitmap = Bitmap.createBitmap(maxWidth, maxHeight, qrBitmap.config)
        val canvas = Canvas(resultBitmap)

        val qrLeft = (maxWidth - qrBitmap.width) / 2
        val qrTop = (maxHeight - qrBitmap.height) / 2
        canvas.drawBitmap(qrBitmap, qrLeft.toFloat(), qrTop.toFloat(), null)

        val selectedLeft = (maxWidth - selectedBitmap.width) / 2
        val selectedTop = (maxHeight - selectedBitmap.height) / 2
        canvas.drawBitmap(selectedBitmap, selectedLeft.toFloat(), selectedTop.toFloat(), null)

        return resultBitmap
    }

    private fun saveImageToGallery(combinedBitmap: Bitmap) {
        // Save the combined bitmap to the gallery
        val imageFileName = "QRCodeImage_${System.currentTimeMillis()}.jpg"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        val resolver = contentResolver
        val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val uri = resolver.insert(contentUri, contentValues)

        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                combinedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }

            Toast.makeText(this, "Image saved to Gallery", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        // Get the bitmap extra from the intent
        val bitmap = intent.getParcelableExtra<Bitmap>("QR_CODE_BITMAP")
        if (bitmap != null) { originalQrCodeBitmap = bitmap
        } // Save the original QR code image bitmap
        val selectedType = intent.getStringExtra("selectedType")
        val enteredText = intent.getStringExtra("enteredText")
        val time = intent.getStringExtra("time")
        val date = intent.getStringExtra("date")
        val intent = Intent(this, GeneratedQrCodeResultActivity::class.java)
        intent.putExtra("QR_CODE_BITMAP", bitmap) // Pass the bitmap back
        intent.putExtra("time", time)
        intent.putExtra("date", date)
        intent.putExtra("selectedType", selectedType)
        intent.putExtra("enteredText", enteredText)
        startActivity(intent)
        finish()
    }

}