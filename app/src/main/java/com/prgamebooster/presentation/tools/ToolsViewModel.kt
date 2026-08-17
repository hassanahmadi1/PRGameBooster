package com.prgamebooster.presentation.tools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.prgamebooster.domain.model.*
import com.prgamebooster.domain.repository.GameProfileRepository
import com.prgamebooster.domain.repository.SettingsRepository
import com.prgamebooster.games.GameLauncher
import com.prgamebooster.games.LaunchResult
import com.prgamebooster.performance.HapticController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ToolsUiState(
    val settings: AppSettings = AppSettings(),
    val activeProfile: GameProfile? = null,
    val hapticSupported: Boolean = false
)

@HiltViewModel
class ToolsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val gameProfileRepository: GameProfileRepository,
    val gameLauncher: GameLauncher,
    val hapticController: HapticController
) : ViewModel() {

    val uiState: StateFlow<ToolsUiState> = settingsRepository.observeSettings()
        .flatMapLatest { settings ->
            gameProfileRepository.observeProfile(settings.activeGameId).map { profile ->
                ToolsUiState(
                    settings = settings,
                    activeProfile = profile,
                    hapticSupported = hapticController.isSupported
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ToolsUiState())

    fun setFloatingPanel(enabled: Boolean) = viewModelScope.launch {
        settingsRepository.setFloatingPanelEnabled(enabled)
    }

    fun setHaptic(enabled: Boolean) = viewModelScope.launch {
        settingsRepository.setHapticFeedbackEnabled(enabled)
    }

    fun setFullscreen(enabled: Boolean) = viewModelScope.launch {
        settingsRepository.setFullscreenModeEnabled(enabled)
    }

    fun updateProfile(mutator: (GameProfile) -> GameProfile) {
        val current = uiState.value.activeProfile ?: return
        viewModelScope.launch {
            gameProfileRepository.updateProfile(mutator(current))
        }
    }

    fun launchGame(packageName: String): LaunchResult = gameLauncher.launch(packageName)
}
