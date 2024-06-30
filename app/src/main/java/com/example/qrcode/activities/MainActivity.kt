package com.example.qrcode.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
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

class MainActivity : AppCompatActivity() {

    private lateinit var scannerCard : ImageView
    private lateinit var historyCard : ImageView
    private lateinit var settingsCard : ImageView
    private lateinit var generatorCard : ImageView
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        shimmerFrameLayout = findViewById(R.id.shimmer_view_container_main)


        MobileAds.initialize(this) {
            // Displaying a log that AdMob ads have been initialized.
            Log.i("Admob", "Admob Initialized.")
        }

        val adView: AdView = findViewById(R.id.adView_activity_main)

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
                // Handle ad loading failure
            }
        }




        historyCard = findViewById(R.id.home_screen_history_card)
        generatorCard = findViewById(R.id.qr_code_generator_card)
        settingsCard = findViewById(R.id.settings_card_icon_home_screen)
        scannerCard = findViewById(R.id.qr_code_scanner_card)
        scannerCard.setOnClickListener {

            showProgressDialog() // Show the progress dialog before loading the ad

            MobileAds.initialize(this) {
                // on below line displaying a log that admob ads has been initialized.
                Log.i("Admob", "Admob Initialized.")
            }
            // on below line creating and initializing variable for adRequest
            val adRequestInterstitialAd = AdRequest.Builder().build()
            // on below line we are loading interstitial ads setting ad unit id to it, ads request and callback for it.
            InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequestInterstitialAd, object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    val intent = Intent(this@MainActivity, QrCodeScreenActivity::class.java)
                    startActivity(intent)
                    finish()
                    // this method is called when ad is loaded in that case we are displaying our ad.
                    interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {


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
                    interstitialAd.show(this@MainActivity)

                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // this method is called when we get any error
                    Toast.makeText(this@MainActivity, "Fail to load ad..", Toast.LENGTH_SHORT).show()
                    dismissProgressDialog() // Dismiss the progress dialog if ad loading fails
                }

            })

        }

        historyCard.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
            finish()
        }

        settingsCard.setOnClickListener {
            showProgressDialog() // Show the progress dialog before loading the ad

            MobileAds.initialize(this) {
                // on below line displaying a log that admob ads has been initialized.
                Log.i("Admob", "Admob Initialized.")
            }
            // on below line creating and initializing variable for adRequest
            val adRequestInterstitialAd = AdRequest.Builder().build()
            InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequestInterstitialAd, object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                    startActivity(intent)
                    finish()
                    // this method is called when ad is loaded in that case we are displaying our ad.
                    interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {

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
                    interstitialAd.show(this@MainActivity)

                }


                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // this method is called when we get any error
                    Toast.makeText(this@MainActivity, "Fail to load ad..", Toast.LENGTH_SHORT).show()
                    dismissProgressDialog() // Dismiss the progress dialog if ad loading fails
                }

            })

        }


        generatorCard.setOnClickListener {
            val intent = Intent(this, QrGeneratorActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, SplashScreenActivity::class.java)
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