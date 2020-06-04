package com.github.chizoba.compositionandroid

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.lang.ref.WeakReference

const val BROADCAST_ACTION = "example_action"

class BroadcastReceiverDelegate(
    activity: AppCompatActivity
) : BroadcastReceiver(), LifecycleObserver {
    private val lifecycleOwner = requireNotNull(WeakReference(activity).get())

    init {
        lifecycleOwner.lifecycle.addObserver(this@BroadcastReceiverDelegate)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private fun onResume() {
        LocalBroadcastManager.getInstance(lifecycleOwner.applicationContext)
            .registerReceiver(this, IntentFilter(BROADCAST_ACTION))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private fun onPause() {
        LocalBroadcastManager.getInstance(lifecycleOwner.applicationContext)
            .unregisterReceiver(this)
    }

    override fun onReceive(context: Context, intent: Intent) {
        StringBuilder().apply {
            append("Action: ${intent.action}\n")
            append("URI: ${intent.toUri(Intent.URI_INTENT_SCHEME)}\n")
            toString().also { log ->
                Toast.makeText(context, log, Toast.LENGTH_LONG).show()
            }
        }
    }
}
