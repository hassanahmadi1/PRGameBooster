package com.prgamebooster.performance

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import javax.inject.Inject
import javax.inject.Singleton

/**
 * بازخورد لرزشی واقعی. اگر دستگاه Vibrator نداشته باشد، صادقانه false برمی‌گرداند
 * و لایه UI باید پیام «پشتیبانی نمی‌شود» را نشان دهد؛ هیچ شبیه‌سازی‌ای انجام نمی‌شود.
 */
@Singleton
class HapticController @Inject constructor(
    private val context: Context
) {
    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    val isSupported: Boolean
        get() = vibrator?.hasVibrator() == true

    fun tick() {
        val v = vibrator ?: return
        if (!v.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            v.vibrate(20)
        }
    }
}
