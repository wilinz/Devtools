package com.wilinz.devtools

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.app.Activity
import android.app.Service
import android.content.Intent
import android.graphics.Path
import android.graphics.Rect
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction
import android.view.accessibility.AccessibilityWindowInfo
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat
import com.wilinz.accessbilityx.AccessibilityxService
import com.wilinz.accessbilityx.goAccessibilityServiceSettings
import kotlin.time.Duration

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