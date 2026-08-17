package com.prgamebooster.domain.model

/**
 * پروفایل مستقل هر بازی. هر بازی تنظیمات جدا و ذخیره‌شده خودش را دارد.
 * هیچ‌کدام از این مقادیر بدون کنش صریح کاربر یا API واقعی سیستم تغییر نمی‌کند.
 */
data class GameProfile(
    val id: GameId,
    val displayName: String,
    val packageName: String,
    val targetFps: Int,
    val frameSmoothness: FrameSmoothness,
    val antiLagEnabled: Boolean,
    val gpuAccelerationEnabled: Boolean,
    val networkOptimizationEnabled: Boolean,
    val overlayEnabled: Boolean,
    val crosshairEnabled: Boolean,
    val batterySaverEnabled: Boolean
) {
    companion object {
        /** مقادیر FPS مجاز طبق مشخصات پروژه. هر مقدار دیگری نامعتبر است. */
        val ALLOWED_FPS_VALUES = listOf(30, 40, 45, 60, 90, 120, 144)

        fun isValidFps(fps: Int): Boolean = fps in ALLOWED_FPS_VALUES

        fun default(id: GameId): GameProfile = when (id) {
            GameId.PUBG_MOBILE -> GameProfile(
                id = id,
                displayName = "PUBG Mobile",
                packageName = "com.tencent.ig",
                targetFps = 90,
                frameSmoothness = FrameSmoothness.MEDIUM,
                antiLagEnabled = true,
                gpuAccelerationEnabled = true,
                networkOptimizationEnabled = true,
                overlayEnabled = false,
                crosshairEnabled = false,
                batterySaverEnabled = false
            )
            GameId.FREE_FIRE -> GameProfile(
                id = id,
                displayName = "Free Fire",
                packageName = "com.dts.freefireth",
                targetFps = 60,
                frameSmoothness = FrameSmoothness.MEDIUM,
                antiLagEnabled = true,
                gpuAccelerationEnabled = true,
                networkOptimizationEnabled = true,
                overlayEnabled = false,
                crosshairEnabled = false,
                batterySaverEnabled = false
            )
            GameId.COD_MOBILE -> GameProfile(
                id = id,
                displayName = "Call of Duty Mobile",
                packageName = "com.activision.callofduty.shooter",
                targetFps = 120,
                frameSmoothness = FrameSmoothness.ULTRA,
                antiLagEnabled = false,
                gpuAccelerationEnabled = true,
                networkOptimizationEnabled = true,
                overlayEnabled = false,
                crosshairEnabled = false,
                batterySaverEnabled = false
            )
        }
    }
}

enum class GameId { PUBG_MOBILE, FREE_FIRE, COD_MOBILE }

enum class FrameSmoothness { LOW, MEDIUM, ULTRA }
