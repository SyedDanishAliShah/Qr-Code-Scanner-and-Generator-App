package com.example.qrcode.activities.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.qrcode.R
import com.example.qrcode.activities.DecorateQrCodeActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_layout, container, false)

        val imageView1 = view.findViewById<ImageView>(R.id.fb_logo_bottom_sheet)
        val imageView2 = view.findViewById<ImageView>(R.id.paypal_logo_bottom_sheet)
        val imageView3 = view.findViewById<ImageView>(R.id.messenger_logo_bottom_sheet)
        val imageView4 = view.findViewById<ImageView>(R.id.whatsapp_logo_bottom_sheet)
        val imageView5 = view.findViewById<ImageView>(R.id.skype_logo_bottom_sheet)
        val imageView6 = view.findViewById<ImageView>(R.id.pinterest_logo_bottom_sheet)
        val imageView7 = view.findViewById<ImageView>(R.id.mastercard_logo_bottom_sheet)
        val imageView8 = view.findViewById<ImageView>(R.id.twitter_logo_bottom_sheet)
        val imageView9 = view.findViewById<ImageView>(R.id.youtube_logo_bottom_sheet)
        val imageView10 = view.findViewById<ImageView>(R.id.bitcoin_logo_bottom_sheet)
        val imageView11 = view.findViewById<ImageView>(R.id.coins_logo_bottom_sheet)
        val imageView12 = view.findViewById<ImageView>(R.id.linkedin_logo_bottom_sheet)
        val saveButton = view.findViewById<ImageView>(R.id.save_button_rectangle_bottom_sheet)
        val discardButton = view.findViewById<ImageView>(R.id.discard_rectangle_bottom_sheet)

        saveButton.setOnClickListener {
            dismiss()
        }

        discardButton.setOnClickListener{
            val activity = activity
            if (activity is DecorateQrCodeActivity) {
                activity.clearSelectedImage()
            }
            dismiss()
        }

        imageView1.setOnClickListener {
            sendSelectedImageToActivity(R.drawable.fb_icon_bottom_sheet)
        }

        imageView2.setOnClickListener {
            sendSelectedImageToActivity(R.drawable.paypal_icon_bottom_sheet)
        }

        imageView3.setOnClickListener {
            sendSelectedImageToActivity(R.drawable.messenger_logo_bottom_sheet)
        }

        imageView4.setOnClickListener {
            sendSelectedImageToActivity(R.drawable.whatsapp_logo_bottom_sheet)
        }

        imageView5.setOnClickListener {
            sendSelectedImageToActivity(R.drawable.skype_logo_bottom_sheet)
        }

        imageView6.setOnClickListener {
            sendSelectedImageToActivity(R.drawable.pinterest_logo_bottom_sheet)
        }

        imageView7.setOnClickListener {
            sendSelectedImageToActivity(R.drawable.master_card_logo_bottom_sheet)
        }

        imageView8.setOnClickListener {
            sendSelectedImageToActivity(R.drawable.twitter_logo_bottom_sheet)
        }

        imageView9.setOnClickListener {
            sendSelectedImageToActivity(R.drawable.youtube_icon_bottom_sheet)
        }

        imageView10.setOnClickListener {
            sendSelectedImageToActivity(R.drawable.bitcoin_icon_bottom_sheet)
        }

        imageView11.setOnClickListener {
            sendSelectedImageToActivity(R.drawable.coins_icon_bottom_sheet)
        }

        imageView12.setOnClickListener {
            sendSelectedImageToActivity(R.drawable.linkedin_icon_bottom_sheet)
        }

        return view
    }

    private fun sendSelectedImageToActivity(imageResId: Int) {
        val activity = activity
        if (activity is DecorateQrCodeActivity) {
            activity.applySelectedImage(imageResId)
        }
        dismiss()
    }

}