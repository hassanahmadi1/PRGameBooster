package com.prgamebooster.data.repository

import com.prgamebooster.domain.model.BatteryState
import com.prgamebooster.domain.model.LatencyResult
import com.prgamebooster.domain.model.NetworkState
import com.prgamebooster.domain.repository.DeviceMonitorRepository
import com.prgamebooster.performance.BatteryMonitor
import com.prgamebooster.performance.NetworkMonitor
import com.prgamebooster.performance.LatencyMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceMonitorRepositoryImpl @Inject constructor(
    private val batteryMonitor: BatteryMonitor,
    private val networkMonitor: NetworkMonitor,
    private val latencyMonitor: LatencyMonitor
) : DeviceMonitorRepository {

    override fun observeBatteryState(): Flow<BatteryState> = batteryMonitor.observe()

    override fun observeNetworkState(): Flow<NetworkState> = networkMonitor.observeState()

    override fun observeNetworkSpeedMbps(): Flow<Double?> = flow {
        emit(networkMonitor.currentDownstreamMbps())
    }

    override suspend fun measureLatencyOnce(): LatencyResult = latencyMonitor.measureOnce()
}
