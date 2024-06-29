package com.example.qrcode.activities.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import com.example.qrcode.R
import com.example.qrcode.activities.DecorateQrCodeActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomSheetFragmentText : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_layout_text, container, false)

        val editText = view.findViewById<EditText>(R.id.editText)
        val saveButton = view.findViewById<ImageView>(R.id.save_button_rectangle_bottom_sheet_qr_design_text)
        val discardButton = view.findViewById<ImageView>(R.id.discard_rectangle_bottom_sheet_qr_design_text)

        saveButton.setOnClickListener {
            dismiss()
        }
        discardButton.setOnClickListener {
            dismiss()
        }


        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val activity = activity
                if (activity is DecorateQrCodeActivity) {
                    activity.applySelectedText(s.toString())
                }
            }
        })

        return view
    }
}