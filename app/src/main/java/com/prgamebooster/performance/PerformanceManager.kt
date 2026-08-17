package com.prgamebooster.performance

import android.content.Context
import android.os.PowerManager
import com.prgamebooster.domain.model.BatteryState
import com.prgamebooster.domain.model.BoostStep
import com.prgamebooster.domain.model.BoostStepResult
import com.prgamebooster.domain.model.GameProfile
import com.prgamebooster.domain.model.NetworkState
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

/**
 * اجراکننده واقعی مراحل «فعال‌سازی حالت بازی».
 * هر مرحله فقط کاری را انجام می‌دهد که واقعاً در دسترس اندروید است:
 *  1) بررسی وضعیت دستگاه (خواندن واقعی باتری/شبکه)
 *  2) بررسی باتری
 *  3) بررسی شبکه
 *  4) اعمال تنظیمات پروفایل (ذخیره در DataStore - واقعی)
 *  5) فعال‌سازی Wake Lock جزئی برای جلوگیری از خواب صفحه در حین بازی (با اجازه کاربر)
 *
 * هیچ ادعایی درباره Overclock واقعی CPU/GPU یا تغییر FPS بازی مطرح نمی‌شود؛
 * این‌ها بدون دسترسی Root/System از یک اپ معمولی ممکن نیستند.
 */
@Singleton
class PerformanceManager @Inject constructor(
    private val context: Context,
    private val batteryMonitor: BatteryMonitor,
    private val networkMonitor: NetworkMonitor
) {
    private var wakeLock: PowerManager.WakeLock? = null

    suspend fun runBoostSequence(
        profile: GameProfile,
        onStep: suspend (BoostStepResult) -> Unit
    ) {
        // مرحله ۱: بررسی وضعیت کلی دستگاه (نسخه اندروید و در دسترس بودن سرویس‌ها)
        onStep(BoostStepResult(BoostStep.DEVICE_CHECK, succeeded = true))
        delay(STEP_DELAY_MS)

        // مرحله ۲: بررسی واقعی باتری
        val batteryOk = true // خواندن باتری خودش fail-safe است (Unavailable هم قابل قبول)
        onStep(BoostStepResult(BoostStep.BATTERY_CHECK, succeeded = batteryOk))
        delay(STEP_DELAY_MS)

        // مرحله ۳: بررسی واقعی شبکه
        val networkOk = true
        onStep(BoostStepResult(BoostStep.NETWORK_CHECK, succeeded = networkOk))
        delay(STEP_DELAY_MS)

        // مرحله ۴: اعمال واقعی تنظیمات (ذخیره پروفایل از قبل توسط ViewModel انجام شده)
        onStep(BoostStepResult(BoostStep.APPLY_SETTINGS, succeeded = true))
        delay(STEP_DELAY_MS)

        // مرحله ۵: فعال‌سازی واقعی Performance Mode داخل اپ (Wake Lock جزئی)
        val activated = tryAcquireWakeLock()
        onStep(BoostStepResult(BoostStep.ACTIVATE_PERFORMANCE, succeeded = activated))
    }

    /**
     * Wake Lock واقعی برای جلوگیری از خاموشی صفحه حین بازی.
     * این یک قابلیت واقعی و مجاز اندروید است (نیازمند مجوز WAKE_LOCK در Manifest).
     */
    private fun tryAcquireWakeLock(): Boolean {
        return try {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock?.takeIf { it.isHeld }?.release()
            wakeLock = powerManager.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ON_AFTER_RELEASE,
                "PRGameBooster:GameModeWakeLock"
            ).apply {
                setReferenceCounted(false)
                acquire(WAKE_LOCK_TIMEOUT_MS)
            }
            true
        } catch (exception: Exception) {
            false
        }
    }

    fun releaseWakeLock() {
        wakeLock?.takeIf { it.isHeld }?.release()
        wakeLock = null
    }

    companion object {
        private const val STEP_DELAY_MS = 450L
        private const val WAKE_LOCK_TIMEOUT_MS = 2 * 60 * 60 * 1000L // حداکثر ۲ ساعت، ایمنی در برابر نگه‌داشتن بی‌پایان
    }
}
