package com.wilinz.devtools.service

import android.content.Intent
import android.provider.Settings
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.wilinz.accessbilityx.AccessibilityxService
import com.wilinz.devtools.App

class AutoAccessibilityService : AccessibilityxService() {

    companion object {

        var instance: AutoAccessibilityService? = null
            private set
            get() {
                if (field == null) {
                    App.instance.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                        this.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                    Toast.makeText(App.instance, "请打开无障碍", Toast.LENGTH_SHORT).show()
                }
                return field
            }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Toast.makeText(this, "无障碍已打开", Toast.LENGTH_SHORT).show()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        super.onAccessibilityEvent(event)
    }


    override fun onInterrupt() {
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }
}