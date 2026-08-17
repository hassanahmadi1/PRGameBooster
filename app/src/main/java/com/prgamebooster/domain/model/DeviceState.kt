package com.prgamebooster.domain.model

/**
 * تمام مقادیر این فایل باید مستقیماً از API واقعی اندروید بیایند.
 * هر مقداری که در دسترس نباشد باید Unavailable برگردانده شود، نه صفر یا Random.
 */
sealed class BatteryState {
    data class Available(
        val percentage: Int,
        val isCharging: Boolean
    ) : BatteryState()

    object Unavailable : BatteryState()
}

sealed class NetworkState {
    data class Connected(
        val type: NetworkType,
        val isValidated: Boolean
    ) : NetworkState()

    object Disconnected : NetworkState()
}

enum class NetworkType { WIFI, CELLULAR_5G, CELLULAR_4G, CELLULAR_OTHER, ETHERNET, UNKNOWN }

sealed class LatencyResult {
    data class Measured(val millis: Long, val quality: LatencyQuality) : LatencyResult()
    object Unavailable : LatencyResult()
    object NotTestedYet : LatencyResult()
}

enum class LatencyQuality { EXCELLENT, GOOD, MEDIUM, POOR }

sealed class FpsReading {
    data class Actual(val fps: Int) : FpsReading()
    object Unavailable : FpsReading()
}

data class BoostStepResult(
    val step: BoostStep,
    val succeeded: Boolean,
    val detailMessageResId: Int? = null
)

enum class BoostStep { DEVICE_CHECK, BATTERY_CHECK, NETWORK_CHECK, APPLY_SETTINGS, ACTIVATE_PERFORMANCE }
