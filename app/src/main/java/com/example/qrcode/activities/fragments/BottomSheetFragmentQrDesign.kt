package com.example.qrcode.activities.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.qrcode.R
import com.example.qrcode.activities.DecorateQrCodeActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetFragmentQrDesign : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_layout_qr_design, container, false)

        val imageView1 = view.findViewById<ImageView>(R.id.black_color_qr_design)
        val imageView2 = view.findViewById<ImageView>(R.id.grey_color_qr_design)
        val imageView3 = view.findViewById<ImageView>(R.id.dark_pink_color_qr_design)
        val imageView4 = view.findViewById<ImageView>(R.id.pink_color_qr_design)
        val imageView5 = view.findViewById<ImageView>(R.id.purple_color_qr_design)
        val imageView6 = view.findViewById<ImageView>(R.id.dark_blue_color_qr_design)
        val imageView7 = view.findViewById<ImageView>(R.id.light_blue_color_qr_design)
        val imageView8 = view.findViewById<ImageView>(R.id.sky_blue_color_qr_design)
        val imageView9 = view.findViewById<ImageView>(R.id.light_green_color_qr_design)
        val imageView10 = view.findViewById<ImageView>(R.id.dark_green_color_qr_design)
        val imageView11 = view.findViewById<ImageView>(R.id.yellow_color_qr_design)
        val imageView12 = view.findViewById<ImageView>(R.id.red_color_qr_design)
        val imageView13 = view.findViewById<ImageView>(R.id.grey_color_qr_design_1)
        val imageView14 = view.findViewById<ImageView>(R.id.brown_color_qr_design)
        val imageView15 = view.findViewById<ImageView>(R.id.light_blue_color_qr_design_1)
        val saveButton = view.findViewById<ImageView>(R.id.save_button_rectangle_bottom_sheet_qr_design)
        val discardButton = view.findViewById<ImageView>(R.id.discard_rectangle_bottom_sheet_qr_design)

        saveButton.setOnClickListener {
            dismiss()
        }

        discardButton.setOnClickListener {
            dismiss()
        }

        imageView1.setOnClickListener {
            sendSelectedColorToActivity(R.color.black)
        }

        imageView2.setOnClickListener {
            sendSelectedColorToActivity(R.color.grey)
        }

        imageView3.setOnClickListener {
            sendSelectedColorToActivity(R.color.color_dark_pink)
        }

        imageView4.setOnClickListener {
            sendSelectedColorToActivity(R.color.color_pink)
        }

        imageView5.setOnClickListener {
            sendSelectedColorToActivity(R.color.color_purple)
        }

        imageView6.setOnClickListener {
            sendSelectedColorToActivity(R.color.color_light_blue)
        }

        imageView7.setOnClickListener {
            sendSelectedColorToActivity(R.color.color_dark_blue)
        }

        imageView8.setOnClickListener {
            sendSelectedColorToActivity(R.color.color_sky_blue)
        }

        imageView9.setOnClickListener {
            sendSelectedColorToActivity(R.color.color_light_green)
        }

        imageView10.setOnClickListener {
            sendSelectedColorToActivity(R.color.color_dark_green)
        }

        imageView11.setOnClickListener {
            sendSelectedColorToActivity(R.color.color_yellow)
        }

        imageView12.setOnClickListener {
            sendSelectedColorToActivity(R.color.color_red)
        }

        imageView13.setOnClickListener {
            sendSelectedColorToActivity(R.color.grey)
        }

        imageView14.setOnClickListener {
            sendSelectedColorToActivity(R.color.color_brown)
        }

        imageView15.setOnClickListener {
            sendSelectedColorToActivity(R.color.color_dark_blue)
        }



        return view
    }

    private fun sendSelectedColorToActivity(drawableResId: Int) {
        val activity = activity
        if (activity is DecorateQrCodeActivity) {
            activity.applySelectedColor(drawableResId)
        }
        dismiss()
    }
}