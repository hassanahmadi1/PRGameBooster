package com.prgamebooster.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prgamebooster.domain.model.*
import com.prgamebooster.domain.repository.DeviceMonitorRepository
import com.prgamebooster.domain.repository.GameProfileRepository
import com.prgamebooster.domain.repository.SettingsRepository
import com.prgamebooster.performance.PerformanceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val activeGameId: GameId = GameId.PUBG_MOBILE,
    val profiles: List<GameProfile> = emptyList(),
    val activeProfile: GameProfile? = null,
    val gameModeActive: Boolean = false,
    val batteryState: BatteryState = BatteryState.Unavailable,
    val networkState: NetworkState = NetworkState.Disconnected,
    val boostInProgress: Boolean = false,
    val boostStepMessageResId: Int? = null,
    val toastMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val gameProfileRepository: GameProfileRepository,
    private val settingsRepository: SettingsRepository,
    private val deviceMonitorRepository: DeviceMonitorRepository,
    private val performanceManager: PerformanceManager
) : ViewModel() {

    private val _boostInProgress = MutableStateFlow(false)
    private val _boostStepMessageResId = MutableStateFlow<Int?>(null)
    private val _toastMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<HomeUiState> = combine(
        settingsRepository.observeSettings(),
        gameProfileRepository.observeAllProfiles(),
        deviceMonitorRepository.observeBatteryState(),
        deviceMonitorRepository.observeNetworkState(),
        _boostInProgress,
        _boostStepMessageResId
    ) { flows ->
        val settings = flows[0] as AppSettings
        @Suppress("UNCHECKED_CAST")
        val profiles = flows[1] as List<GameProfile>
        val battery = flows[2] as BatteryState
        val network = flows[3] as NetworkState
        val inProgress = flows[4] as Boolean
        val stepMsg = flows[5] as Int?

        HomeUiState(
            activeGameId = settings.activeGameId,
            profiles = profiles,
            activeProfile = profiles.find { it.id == settings.activeGameId },
            gameModeActive = settings.gameModeActive,
            batteryState = battery,
            networkState = network,
            boostInProgress = inProgress,
            boostStepMessageResId = stepMsg
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    fun selectGame(gameId: GameId) {
        viewModelScope.launch {
            settingsRepository.setActiveGame(gameId)
        }
    }

    fun activateGameMode() {
        val profile = uiState.value.activeProfile ?: return
        if (_boostInProgress.value) return

        viewModelScope.launch {
            _boostInProgress.value = true
            performanceManager.runBoostSequence(profile) { stepResult ->
                _boostStepMessageResId.value = when (stepResult.step) {
                    BoostStep.DEVICE_CHECK -> com.prgamebooster.R.string.home_step_checking_device
                    BoostStep.BATTERY_CHECK -> com.prgamebooster.R.string.home_step_checking_battery
                    BoostStep.NETWORK_CHECK -> com.prgamebooster.R.string.home_step_checking_network
                    BoostStep.APPLY_SETTINGS -> com.prgamebooster.R.string.home_step_applying
                    BoostStep.ACTIVATE_PERFORMANCE -> com.prgamebooster.R.string.home_step_done
                }
            }
            settingsRepository.setGameModeActive(true)
            _boostInProgress.value = false
            _boostStepMessageResId.value = null
        }
    }

    fun deactivateGameMode() {
        viewModelScope.launch {
            settingsRepository.setGameModeActive(false)
            performanceManager.releaseWakeLock()
        }
    }

    fun consumeToast() {
        _toastMessage.value = null
    }
}
