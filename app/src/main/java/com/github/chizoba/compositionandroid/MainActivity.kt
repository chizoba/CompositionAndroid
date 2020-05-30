package com.github.chizoba.compositionandroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.disableButton
import kotlinx.android.synthetic.main.activity_main.enableButton

class MainActivity : AppCompatActivity() {
    private val screenshotSecurityDelegate = ScreenshotSecurityDelegate(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
    }

    private fun initDelegates() {
        lifecycle.addObserver(screenshotSecurityDelegate)
    }

    private fun initViews() {
        enableButton.setOnClickListener {
            screenshotSecurityDelegate.enableScreenshotUsage()
            enableButton.isEnabled = false
            disableButton.isEnabled = true
        }

        disableButton.setOnClickListener {
            screenshotSecurityDelegate.disableScreenshotUsage()
            disableButton.isEnabled = false
            enableButton.isEnabled = true
        }
    }
}
