package com.prgamebooster.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prgamebooster.domain.model.AppSettings
import com.prgamebooster.domain.model.GameId
import com.prgamebooster.domain.repository.GameProfileRepository
import com.prgamebooster.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val gameProfileRepository: GameProfileRepository
) : ViewModel() {

    val settings: StateFlow<AppSettings> = settingsRepository.observeSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    fun setFloatingPanel(enabled: Boolean) = viewModelScope.launch { settingsRepository.setFloatingPanelEnabled(enabled) }
    fun setHaptic(enabled: Boolean) = viewModelScope.launch { settingsRepository.setHapticFeedbackEnabled(enabled) }
    fun setAutoRefresh(enabled: Boolean) = viewModelScope.launch { settingsRepository.setAutoRefreshEnabled(enabled) }
    fun setFullscreen(enabled: Boolean) = viewModelScope.launch { settingsRepository.setFullscreenModeEnabled(enabled) }

    fun resetAll() = viewModelScope.launch {
        settingsRepository.resetToDefaults()
        gameProfileRepository.resetAllProfiles()
    }

    fun resetProfile(gameId: GameId) = viewModelScope.launch {
        gameProfileRepository.resetProfile(gameId)
    }
}
