package com.example.qrcode.activities

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.qrcode.R
import com.example.qrcode.activities.fragments.GenerateQrCodeFragment
import com.example.qrcode.activities.fragments.ReadQrCodeFragment
import com.google.android.material.tabs.TabLayout

class HistoryActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var simpleFrameLayout: FrameLayout
    private lateinit var backIcon : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        tabLayout = findViewById(R.id.tab_layout)
        simpleFrameLayout = findViewById(R.id.simpleFrameLayout)
        backIcon = findViewById(R.id.back_icon_2)

        backIcon.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val firstTab = tabLayout.newTab()
        firstTab.text = "Read Qr Code"
        tabLayout.addTab(firstTab)

        val secondTab = tabLayout.newTab()
        secondTab.text = "Generate Qr Code"
        tabLayout.addTab(secondTab)

        // Manually select the first tab and add the corresponding fragment
        tabLayout.selectTab(firstTab)
        val fragment = ReadQrCodeFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.simpleFrameLayout, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                var fragment: Fragment? = null
                when (tab.position) {
                    0 -> fragment = ReadQrCodeFragment()
                    1 -> fragment = GenerateQrCodeFragment()
                }
                supportFragmentManager.beginTransaction()
                    .replace(R.id.simpleFrameLayout, fragment!!)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
}
