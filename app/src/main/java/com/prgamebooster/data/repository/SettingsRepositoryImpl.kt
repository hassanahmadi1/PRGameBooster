package com.prgamebooster.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.prgamebooster.data.datastore.SettingsKeys
import com.prgamebooster.data.datastore.prGameBoosterDataStore
import com.prgamebooster.domain.model.AnimationIntensity
import com.prgamebooster.domain.model.AppSettings
import com.prgamebooster.domain.model.GameId
import com.prgamebooster.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val context: Context
) : SettingsRepository {

    private val dataStore get() = context.prGameBoosterDataStore

    override fun observeSettings(): Flow<AppSettings> = dataStore.data.map { prefs ->
        AppSettings(
            floatingPanelEnabled = prefs[SettingsKeys.FLOATING_PANEL_ENABLED] ?: false,
            hapticFeedbackEnabled = prefs[SettingsKeys.HAPTIC_ENABLED] ?: true,
            autoRefreshEnabled = prefs[SettingsKeys.AUTO_REFRESH_ENABLED] ?: true,
            fullscreenModeEnabled = prefs[SettingsKeys.FULLSCREEN_ENABLED] ?: false,
            textScale = prefs[SettingsKeys.TEXT_SCALE] ?: 1.0f,
            animationIntensity = prefs[SettingsKeys.ANIMATION_INTENSITY]
                ?.let { runCatching { AnimationIntensity.valueOf(it) }.getOrNull() }
                ?: AnimationIntensity.NORMAL,
            activeGameId = prefs[SettingsKeys.ACTIVE_GAME_ID]
                ?.let { runCatching { GameId.valueOf(it) }.getOrNull() }
                ?: GameId.PUBG_MOBILE,
            gameModeActive = prefs[SettingsKeys.GAME_MODE_ACTIVE] ?: false
        )
    }

    override suspend fun setFloatingPanelEnabled(enabled: Boolean) {
        dataStore.edit { it[SettingsKeys.FLOATING_PANEL_ENABLED] = enabled }
    }

    override suspend fun setHapticFeedbackEnabled(enabled: Boolean) {
        dataStore.edit { it[SettingsKeys.HAPTIC_ENABLED] = enabled }
    }

    override suspend fun setAutoRefreshEnabled(enabled: Boolean) {
        dataStore.edit { it[SettingsKeys.AUTO_REFRESH_ENABLED] = enabled }
    }

    override suspend fun setFullscreenModeEnabled(enabled: Boolean) {
        dataStore.edit { it[SettingsKeys.FULLSCREEN_ENABLED] = enabled }
    }

    override suspend fun setActiveGame(gameId: GameId) {
        dataStore.edit { it[SettingsKeys.ACTIVE_GAME_ID] = gameId.name }
    }

    override suspend fun setGameModeActive(active: Boolean) {
        dataStore.edit { it[SettingsKeys.GAME_MODE_ACTIVE] = active }
    }

    override suspend fun resetToDefaults() {
        dataStore.edit {
            it[SettingsKeys.FLOATING_PANEL_ENABLED] = false
            it[SettingsKeys.HAPTIC_ENABLED] = true
            it[SettingsKeys.AUTO_REFRESH_ENABLED] = true
            it[SettingsKeys.FULLSCREEN_ENABLED] = false
            it[SettingsKeys.TEXT_SCALE] = 1.0f
            it[SettingsKeys.ANIMATION_INTENSITY] = AnimationIntensity.NORMAL.name
            it[SettingsKeys.GAME_MODE_ACTIVE] = false
        }
    }
}
