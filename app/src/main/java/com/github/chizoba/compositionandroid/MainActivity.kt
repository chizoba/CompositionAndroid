package com.github.chizoba.compositionandroid

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.activity_main.broadcastButton
import kotlinx.android.synthetic.main.activity_main.disableButton
import kotlinx.android.synthetic.main.activity_main.enableButton

class MainActivity : AppCompatActivity() {
    private val screenshotSecurityDelegate = ScreenshotSecurityDelegate(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initDelegates()

        initViews()
    }

    private fun initDelegates() {
        BroadcastReceiverDelegate(this)
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

        broadcastButton.setOnClickListener {
            Intent(BROADCAST_ACTION).also { intent ->
                intent.putExtra("extras.firstname", "Chizoba")
                intent.putExtra("extras.surname", "Ogbonna")
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
            }
        }
    }
}
