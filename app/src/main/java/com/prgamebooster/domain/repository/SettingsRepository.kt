package com.prgamebooster.domain.repository

import com.prgamebooster.domain.model.AppSettings
import com.prgamebooster.domain.model.GameId
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSettings(): Flow<AppSettings>
    suspend fun setFloatingPanelEnabled(enabled: Boolean)
    suspend fun setHapticFeedbackEnabled(enabled: Boolean)
    suspend fun setAutoRefreshEnabled(enabled: Boolean)
    suspend fun setFullscreenModeEnabled(enabled: Boolean)
    suspend fun setActiveGame(gameId: GameId)
    suspend fun setGameModeActive(active: Boolean)
    suspend fun resetToDefaults()
}
