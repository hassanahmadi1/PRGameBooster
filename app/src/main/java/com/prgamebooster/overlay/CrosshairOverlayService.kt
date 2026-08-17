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
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout

/**
 * Overlay واقعی نشانه‌گر وسط صفحه با WindowManager سیستم.
 * اندازه و شفافیت از طریق Intent Extras قابل تنظیم است.
 */
class CrosshairOverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var dotView: android.view.View? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val sizeDp = intent?.getIntExtra(EXTRA_SIZE_DP, DEFAULT_SIZE_DP) ?: DEFAULT_SIZE_DP
        val opacityPercent = intent?.getIntExtra(EXTRA_OPACITY_PERCENT, DEFAULT_OPACITY) ?: DEFAULT_OPACITY

        if (dotView == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                stopSelf()
                return super.onStartCommand(intent, flags, startId)
            }
            showCrosshair(sizeDp, opacityPercent)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun showCrosshair(sizeDp: Int, opacityPercent: Int) {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val density = resources.displayMetrics.density
        val sizePx = (sizeDp * density).toInt()
        val alpha = (opacityPercent.coerceIn(0, 100) / 100f)

        val dot = LinearLayout(this).apply {
            setBackgroundColor(Color.parseColor("#F5A623"))
            this.alpha = alpha
        }
        dotView = dot

        val layoutFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE

        val params = WindowManager.LayoutParams(
            sizePx,
            sizePx,
            layoutFlag,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }

        windowManager?.addView(dot, params)
    }

    override fun onDestroy() {
        dotView?.let { view ->
            try {
                windowManager?.removeView(view)
            } catch (exception: IllegalArgumentException) {
                // قبلاً حذف شده
            }
        }
        super.onDestroy()
    }

    companion object {
        const val EXTRA_SIZE_DP = "extra_size_dp"
        const val EXTRA_OPACITY_PERCENT = "extra_opacity_percent"
        const val DEFAULT_SIZE_DP = 8
        const val DEFAULT_OPACITY = 90
    }
}
