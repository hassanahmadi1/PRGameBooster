package com.prgamebooster.domain.repository

import com.prgamebooster.domain.model.BatteryState
import com.prgamebooster.domain.model.LatencyResult
import com.prgamebooster.domain.model.NetworkState
import kotlinx.coroutines.flow.Flow

/**
 * منبع واحد وضعیت واقعی دستگاه. هیچ مقداری در پیاده‌سازی این اینترفیس
 * نباید Random یا Hard-coded باشد؛ فقط BatteryManager/ConnectivityManager واقعی.
 */
interface DeviceMonitorRepository {
    fun observeBatteryState(): Flow<BatteryState>
    fun observeNetworkState(): Flow<NetworkState>
    fun observeNetworkSpeedMbps(): Flow<Double?>
    suspend fun measureLatencyOnce(): LatencyResult
}
