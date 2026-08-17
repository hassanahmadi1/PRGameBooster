package com.prgamebooster.overlay

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView

/**
 * پنل شناور واقعی با WindowManager سیستم (نه یک div داخل WebView).
 * برای اجرا نیازمند مجوز SYSTEM_ALERT_WINDOW است که پیش از فراخوانی این سرویس
 * باید توسط UI بررسی و از کاربر درخواست شده باشد (Settings.canDrawOverlays).
 */
class FloatingPanelService : Service() {

    private var windowManager: WindowManager? = null
    private var panelView: View? = null
    private var latencyLabel: TextView? = null

    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var targetFpsText: String = ""

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        targetFpsText = intent?.getStringExtra(EXTRA_TARGET_FPS_TEXT) ?: targetFpsText
        if (panelView == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                // بدون مجوز واقعی، سرویس هیچ Overlay‌ای رسم نمی‌کند
                stopSelf()
            } else {
                showPanel()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun showPanel() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val container = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(24, 16, 24, 16)
            setBackgroundColor(Color.parseColor("#CC131923"))
        }

        val fpsLabel = TextView(this).apply {
            text = targetFpsText
            setTextColor(Color.parseColor("#F5A623"))
            textSize = 13f
        }

        latencyLabel = TextView(this).apply {
            text = "-- ms"
            setTextColor(Color.parseColor("#F1F4F8"))
            textSize = 13f
            setPadding(24, 0, 0, 0)
        }

        val closeLabel = TextView(this).apply {
            text = "×"
            setTextColor(Color.parseColor("#9AA7B8"))
            textSize = 16f
            setPadding(24, 0, 0, 0)
            setOnClickListener { stopSelf() }
        }

        container.addView(fpsLabel)
        container.addView(latencyLabel)
        container.addView(closeLabel)
        panelView = container

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.END
            x = 24
            y = 120
        }

        // قابلیت Drag واقعی با لمس مستقیم روی View
        container.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX - (event.rawX - initialTouchX).toInt()
                    params.y = initialY - (event.rawY - initialTouchY).toInt()
                    windowManager?.updateViewLayout(view, params)
                    true
                }
                else -> false
            }
        }

        windowManager?.addView(container, params)
    }

    fun updateLatency(millis: Long) {
        latencyLabel?.text = "$millis ms"
    }

    override fun onDestroy() {
        panelView?.let { view ->
            try {
                windowManager?.removeView(view)
            } catch (exception: IllegalArgumentException) {
                // View از قبل حذف شده - مشکلی نیست
            }
        }
        super.onDestroy()
    }

    companion object {
        const val EXTRA_TARGET_FPS_TEXT = "extra_target_fps_text"
    }
}
