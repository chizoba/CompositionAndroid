package com.github.chizoba.compositionandroid

import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import java.lang.ref.WeakReference

class ScreenshotSecurityDelegate(activity: AppCompatActivity) : LifecycleObserver {
    init {
        activity.lifecycle.addObserver(this)
    }

    private val lifecycleOwner = WeakReference(activity).get()

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun start() {
        disableScreenshotUsage()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        lifecycleOwner?.run { lifecycle.removeObserver(this@ScreenshotSecurityDelegate) }
    }

    fun enableScreenshotUsage() {
        lifecycleOwner?.run { window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE) }
    }

    fun disableScreenshotUsage() {
        lifecycleOwner?.run { window.addFlags(WindowManager.LayoutParams.FLAG_SECURE) }
    }
}
