package com.prgamebooster.presentation.monitor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prgamebooster.domain.model.*
import com.prgamebooster.domain.repository.DeviceMonitorRepository
import com.prgamebooster.domain.repository.GameProfileRepository
import com.prgamebooster.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MonitorUiState(
    val batteryState: BatteryState = BatteryState.Unavailable,
    val networkState: NetworkState = NetworkState.Disconnected,
    val downstreamMbps: Double? = null,
    val latencyResult: LatencyResult = LatencyResult.NotTestedYet,
    val isTestingLatency: Boolean = false,
    val targetFps: Int = 0,
    val actualFps: FpsReading = FpsReading.Unavailable
)

@HiltViewModel
class MonitorViewModel @Inject constructor(
    private val deviceMonitorRepository: DeviceMonitorRepository,
    private val settingsRepository: SettingsRepository,
    private val gameProfileRepository: GameProfileRepository
) : ViewModel() {

    private val _latencyResult = MutableStateFlow<LatencyResult>(LatencyResult.NotTestedYet)
    private val _isTestingLatency = MutableStateFlow(false)

    val uiState: StateFlow<MonitorUiState> = combine(
        deviceMonitorRepository.observeBatteryState(),
        deviceMonitorRepository.observeNetworkState(),
        deviceMonitorRepository.observeNetworkSpeedMbps(),
        _latencyResult,
        _isTestingLatency,
        settingsRepository.observeSettings().flatMapLatest { settings ->
            gameProfileRepository.observeProfile(settings.activeGameId)
        }
    ) { flows ->
        val battery = flows[0] as BatteryState
        val network = flows[1] as NetworkState
        val speed = flows[2] as Double?
        val latency = flows[3] as LatencyResult
        val testing = flows[4] as Boolean
        val profile = flows[5] as GameProfile

        MonitorUiState(
            batteryState = battery,
            networkState = network,
            downstreamMbps = speed,
            latencyResult = latency,
            isTestingLatency = testing,
            targetFps = profile.targetFps,
            // اندازه‌گیری واقعی FPS بازی بدون دسترسی سیستمی/Root ممکن نیست؛
            // به‌صورت صادقانه Unavailable نمایش داده می‌شود.
            actualFps = FpsReading.Unavailable
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MonitorUiState())

    fun runLatencyTest() {
        if (_isTestingLatency.value) return
        viewModelScope.launch {
            _isTestingLatency.value = true
            _latencyResult.value = deviceMonitorRepository.measureLatencyOnce()
            _isTestingLatency.value = false
        }
    }
}
