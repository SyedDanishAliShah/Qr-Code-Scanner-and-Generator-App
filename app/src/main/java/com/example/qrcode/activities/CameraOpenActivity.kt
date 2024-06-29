package com.example.qrcode.activities

import android.Manifest
import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import com.example.qrcode.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class CameraOpenActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_PERMISSIONS = 10
    }

    private val requiredPermissions =
        mutableListOf(
            Manifest.permission.CAMERA
        ).toTypedArray()

    private var cameraxManager: CameraxManager? = null

    private lateinit var previewView: PreviewView
    private lateinit var focusRing: ImageView
    private lateinit var animationView: LottieAnimationView
    private lateinit var changeCameraIcon : ImageView
    private lateinit var flashLightOnOffIcon : ImageView
   // private lateinit var capturedBitmap : Bitmap
    private lateinit var galleryIcon: ImageView

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
                val intent = Intent(this@CameraOpenActivity, QrCodeScanResultActivity::class.java)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_open)

        initViews()
        checkCameraPermission()

        animationView.visibility = View.INVISIBLE

        startAnimation()

        changeCameraIcon.setOnClickListener {

            cameraxManager?.changeCameraType()
        }

        flashLightOnOffIcon.setOnClickListener {

            cameraxManager?.changeFlashStatus()
        }

        galleryIcon.setOnClickListener {
            openGallery()
        }

    }


    private fun startAnimation() {
        animationView.setAnimation("Animation - 1709898391426.json")

        animationView.visibility = View.VISIBLE
        animationView.playAnimation()

        // Add a listener to detect when the animation completes
        animationView.addAnimatorListener(object : Animator.AnimatorListener {

            override fun onAnimationStart(p0: Animator) {}

            @SuppressLint("SetTextI18n")
            override fun onAnimationEnd(p0: Animator) {


            }

            override fun onAnimationCancel(p0: Animator) {}

            override fun onAnimationRepeat(p0: Animator) {}
        })
    }

    private fun initViews() {
        previewView = findViewById(R.id.preview_view)
        animationView = findViewById(R.id.scanner_animation_view)
        focusRing = findViewById(R.id.focusRing)
        changeCameraIcon = findViewById(R.id.change_camera_types_icon)
        flashLightOnOffIcon = findViewById(R.id.flash_light_on_off_icon)
        galleryIcon = findViewById(R.id.gallery_icon)
    }

    private fun initCameraManager() {

        cameraxManager = CameraxManager.getInstance(
            this,
            null,
            previewView,
            focusRing,
            1
        )
        cameraxManager?.startCamera()

        cameraxManager?.setReaderFormats(
            ReaderType.FORMAT_QR_CODE.value,
            ReaderType.FORMAT_EAN_8.value,
            ReaderType.FORMAT_EAN_13.value,
            ReaderType.FORMAT_UPC_E.value,
            ReaderType.FORMAT_UPC_A.value,
            ReaderType.FORMAT_AZTEC.value
        )
        cameraxManager?.startReading()

        cameraxManager?.apply {
            // Inside setQrReadSuccessListener
            setQrReadSuccessListener { result ->
                val qrCodeBitmap = generateQrCodeBitmap(result)
                val imagePath = saveBitmapToFile(qrCodeBitmap)

                val intent = Intent(this@CameraOpenActivity, QrCodeScanResultActivity::class.java)
                intent.putExtra("result", result)
                intent.putExtra("imagePath", imagePath)
                // Add other extras if needed

                val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                intent.putExtra("time", currentTime)

                val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                intent.putExtra("date", currentDate)
                startActivity(intent)
            }

// Inside setPhotoCaptureResultListener
            setPhotoCaptureResultListener { capturedBitmap ->
                // This listener is empty because the bitmap is passed to QrCodeScanResultActivity
            }
        }
    }

    // Function to generate a QR code Bitmap from a string
    private fun generateQrCodeBitmap(qrCodeText: String): Bitmap {
        val qrCodeSize = 512 // You can adjust the size as per your requirement
        val barcodeEncoder = BarcodeEncoder()
        return barcodeEncoder.encodeBitmap(qrCodeText, BarcodeFormat.QR_CODE, qrCodeSize, qrCodeSize)
    }

    private fun saveBitmapToFile(bitmap: Bitmap): String {
        val imageFileName = "QR_Code_${System.currentTimeMillis()}.jpg"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(storageDir, imageFileName)

        try {
            val outputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            return imageFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle error
            return ""
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraxManager?.destroyReferences()
    }

    //region Permission Check
    private fun checkCameraPermission() {
        if (allPermissionsGranted()) {
            initCameraManager()
        } else {
            ActivityCompat.requestPermissions(
                this, requiredPermissions, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun allPermissionsGranted() = requiredPermissions.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                initCameraManager()

            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, QrCodeScreenActivity::class.java)
        startActivity(intent)
        finish()
    }


    //endregion Permission Check
}