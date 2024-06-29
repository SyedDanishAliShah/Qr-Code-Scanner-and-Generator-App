package com.example.qrcode.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.qrcode.R
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
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Hashtable
import java.util.Locale

class QrGeneratorActivity  : AppCompatActivity() {

    private lateinit var backIcon : ImageView
    private lateinit var qrCodeGeneratorCardText : ImageView
    private lateinit var qrCodeWifiCard : ImageView
    private lateinit var qrCodePhoneCard : ImageView
    private lateinit var qrCodeWebsiteCard : ImageView
    private lateinit var textTextView : TextView
    private lateinit var wifiTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var websiteTextView: TextView
    private lateinit var qrcodeGeneratorInputRectangle : EditText
    private lateinit var createButton : ImageView
    private var selectedType: String? = null
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private var adLoaded = false // Flag to track if ad has already been loaded and shown
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_generator)

        MobileAds.initialize(this) {
            // Displaying a log that AdMob ads have been initialized.
            Log.i("Admob", "Admob Initialized.")
        }

        val adView: AdView = findViewById(R.id.adView_activity_qr_code_generator)
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container_qr_generator)

        val adRequest: AdRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        shimmerFrameLayout.startShimmer()
        shimmerFrameLayout.visibility = View.VISIBLE

        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                if (!adLoaded) { // Check if ad has not been loaded and shown yet
                    shimmerFrameLayout.hideShimmer()
                    adLoaded = true // Set flag to true to indicate ad has been loaded and shown
                }
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                super.onAdFailedToLoad(adError)
                // Ad failed to load, handle this situation
                Log.e("Admob", "Ad failed to load: ${adError.message}")
            }
        }



        backIcon = findViewById(R.id.back_icon_4)
        qrCodeGeneratorCardText = findViewById(R.id.qr_generator_options_card_text)
        qrCodeWifiCard = findViewById(R.id.qr_generator_options_card_wifi)
        qrCodePhoneCard = findViewById(R.id.qr_generator_options_card_phone)
        qrCodeWebsiteCard = findViewById(R.id.qr_generator_options_card_website)
        textTextView = findViewById(R.id.please_fill_in_the_text_here_tv)
        wifiTextView = findViewById(R.id.password_tv)
        phoneTextView = findViewById(R.id.phone_number_tv)
        websiteTextView = findViewById(R.id.website_address_tv)
        qrcodeGeneratorInputRectangle = findViewById(R.id.qr_code_generator_input_rectangle)
        createButton = findViewById(R.id.qr_code_generator_screen_button)

        val color = ContextCompat.getColor(this, R.color.textCardOnClickColor)

        backIcon.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        qrCodeGeneratorCardText.setOnClickListener {
            resetColorFilters()
            qrCodeGeneratorCardText.setColorFilter(color)
            selectedType = "Text :"
            textTextView.visibility = View.VISIBLE
            wifiTextView.visibility = View.INVISIBLE
            phoneTextView.visibility = View.INVISIBLE
            websiteTextView.visibility = View.INVISIBLE
        }


        qrCodeWifiCard.setOnClickListener {
            resetColorFilters()
            qrCodeWifiCard.setColorFilter(color)
            selectedType = "WiFi :"
            wifiTextView.visibility = View.VISIBLE
            textTextView.visibility = View.INVISIBLE
            phoneTextView.visibility = View.INVISIBLE
            websiteTextView.visibility = View.INVISIBLE
        }

        qrCodePhoneCard.setOnClickListener {
            resetColorFilters()
            qrCodePhoneCard.setColorFilter(color)
            selectedType = "Phone :"
            phoneTextView.visibility = View.VISIBLE
            textTextView.visibility = View.INVISIBLE
            wifiTextView.visibility = View.INVISIBLE
            websiteTextView.visibility = View.INVISIBLE
        }

        qrCodeWebsiteCard.setOnClickListener {
            resetColorFilters()
            qrCodeWebsiteCard.setColorFilter(color)
            selectedType = "Website :"
            websiteTextView.visibility = View.VISIBLE
            textTextView.visibility = View.INVISIBLE
            wifiTextView.visibility = View.INVISIBLE
            phoneTextView.visibility = View.INVISIBLE
        }

        qrcodeGeneratorInputRectangle.setOnClickListener {
            qrcodeGeneratorInputRectangle.requestFocus()
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(
                qrcodeGeneratorInputRectangle,
                InputMethodManager.SHOW_IMPLICIT
            )
        }

        createButton.setOnClickListener {
            showProgressDialog() // Show the progress dialog before loading the ad

            MobileAds.initialize(this@QrGeneratorActivity) {
                // on below line displaying a log that admob ads has been initialized.
                Log.i("Admob", "Admob Initialized.")
            }
            // on below line creating and initializing variable for adRequest
            val adRequestInterstitialAd = AdRequest.Builder().build()
            InterstitialAd.load(
                this@QrGeneratorActivity,
                "ca-app-pub-3940256099942544/1033173712",
                adRequestInterstitialAd,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        // Generate QR code and navigate to the next activity
                        val text = qrcodeGeneratorInputRectangle.text.toString()
                        if (text.isNotEmpty()) {
                            val bitmap = generateQRCode(text)
                            // Pass the bitmap, selected type, and text to the other activity
                            val intent = Intent(
                                this@QrGeneratorActivity,
                                GeneratedQrCodeResultActivity::class.java
                            )
                            intent.putExtra("QR_CODE_BITMAP", bitmap)
                            intent.putExtra("selectedType", selectedType)
                            intent.putExtra("enteredText", text)
                            val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                            intent.putExtra("time", currentTime)
                            val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                            intent.putExtra("date", currentDate)
                            startActivity(intent)
                            finish()


                            // this method is called when ad is loaded in that case we are displaying our ad.
                            interstitialAd.fullScreenContentCallback =
                                object : FullScreenContentCallback() {

                                    override fun onAdDismissedFullScreenContent() {
                                        // Proceed to the next activity after the ad is dismissed
                                        navigateToNextActivity()
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
                            interstitialAd.show(this@QrGeneratorActivity)

                        }
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        // this method is called when we get any error
                        Toast.makeText(
                            this@QrGeneratorActivity,
                            "Fail to load ad..",
                            Toast.LENGTH_SHORT
                        ).show()
                        dismissProgressDialog() // Dismiss the progress dialog if ad loading fails
                    }

                })

        }
        val text = qrcodeGeneratorInputRectangle.text.toString()
        if (text.isNotEmpty()) {
            val bitmap = generateQRCode(text)
            // Pass the bitmap, selected type, and text to the other activity
            val intent = Intent(
                this@QrGeneratorActivity,
                GeneratedQrCodeResultActivity::class.java
            )
            intent.putExtra("QR_CODE_BITMAP", bitmap)
            intent.putExtra("selectedType", selectedType)
            intent.putExtra("enteredText", text)
            val currentTime =
                SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            intent.putExtra("time", currentTime)
            val currentDate =
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            intent.putExtra("date", currentDate)
            startActivity(intent)
        }


    }
                private fun navigateToNextActivity() {
                    val intent =
                        Intent(this@QrGeneratorActivity, GeneratedQrCodeResultActivity::class.java)

                    startActivity(intent)
                }

                private fun generateQRCode(text: String): Bitmap {
                    val width = 450 // QR code width
                    val height = 450 // QR code height

                    val hints = Hashtable<EncodeHintType, ErrorCorrectionLevel>()
                    hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H

                    val bitMatrix = MultiFormatWriter().encode(
                        text,
                        BarcodeFormat.QR_CODE,
                        width,
                        height,
                        hints
                    )
                    val pixels = IntArray(width * height)
                    for (y in 0 until height) {
                        for (x in 0 until width) {
                            pixels[y * width + x] =
                                if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                        }
                    }

                    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
                    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
                    return bitmap
                }
                private fun resetColorFilters() {
                    qrCodeGeneratorCardText.colorFilter = null
                    qrCodeWifiCard.colorFilter = null
                    qrCodePhoneCard.colorFilter = null
                    qrCodeWebsiteCard.colorFilter = null
                }

                @Deprecated("Deprecated in Java")
                override fun onBackPressed() {
                    super.onBackPressed()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
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