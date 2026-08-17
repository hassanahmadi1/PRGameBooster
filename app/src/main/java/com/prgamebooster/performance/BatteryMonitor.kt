package com.prgamebooster.performance

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.prgamebooster.domain.model.BatteryState
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * منبع واقعی وضعیت باتری با android.os.BatteryManager و Sticky Broadcast رسمی اندروید.
 * هیچ مقداری اینجا شبیه‌سازی نمی‌شود؛ اگر سیستم مقدار ندهد Unavailable برمی‌گردد.
 */
@Singleton
class BatteryMonitor @Inject constructor(
    private val context: Context
) {
    fun observe(): Flow<BatteryState> = callbackFlow {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)

        val receiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                trySend(mapIntentToState(intent))
            }
        }

        // ثبت اولیه: sticky intent فوراً آخرین مقدار واقعی را برمی‌گرداند
        val sticky = context.registerReceiver(receiver, filter)
        trySend(mapIntentToState(sticky))

        awaitClose { context.unregisterReceiver(receiver) }
    }

    private fun mapIntentToState(intent: Intent?): BatteryState {
        if (intent == null) return BatteryState.Unavailable

        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (level < 0 || scale <= 0) return BatteryState.Unavailable

        val percentage = (level * 100) / scale
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
            status == BatteryManager.BATTERY_STATUS_FULL

        return BatteryState.Available(percentage = percentage, isCharging = isCharging)
    }
}
