package com.prgamebooster.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.prgamebooster.data.datastore.GameProfileKeys
import com.prgamebooster.data.datastore.prGameBoosterDataStore
import com.prgamebooster.domain.model.FrameSmoothness
import com.prgamebooster.domain.model.GameId
import com.prgamebooster.domain.model.GameProfile
import com.prgamebooster.domain.repository.GameProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameProfileRepositoryImpl @Inject constructor(
    private val context: Context
) : GameProfileRepository {

    private val dataStore get() = context.prGameBoosterDataStore

    override fun observeProfile(gameId: GameId): Flow<GameProfile> {
        val keys = GameProfileKeys(gameId.name)
        val defaults = GameProfile.default(gameId)

        return dataStore.data.map { prefs ->
            GameProfile(
                id = gameId,
                displayName = defaults.displayName,
                packageName = defaults.packageName,
                targetFps = prefs[keys.targetFps] ?: defaults.targetFps,
                frameSmoothness = prefs[keys.frameSmoothness]
                    ?.let { runCatching { FrameSmoothness.valueOf(it) }.getOrNull() }
                    ?: defaults.frameSmoothness,
                antiLagEnabled = prefs[keys.antiLag] ?: defaults.antiLagEnabled,
                gpuAccelerationEnabled = prefs[keys.gpuAcceleration] ?: defaults.gpuAccelerationEnabled,
                networkOptimizationEnabled = prefs[keys.networkOptimization] ?: defaults.networkOptimizationEnabled,
                overlayEnabled = prefs[keys.overlay] ?: defaults.overlayEnabled,
                crosshairEnabled = prefs[keys.crosshair] ?: defaults.crosshairEnabled,
                batterySaverEnabled = prefs[keys.batterySaver] ?: defaults.batterySaverEnabled
            )
        }
    }

    override fun observeAllProfiles(): Flow<List<GameProfile>> = combine(
        observeProfile(GameId.PUBG_MOBILE),
        observeProfile(GameId.FREE_FIRE),
        observeProfile(GameId.COD_MOBILE)
    ) { pubg, freeFire, codm -> listOf(pubg, freeFire, codm) }

    override suspend fun updateProfile(profile: GameProfile) {
        require(GameProfile.isValidFps(profile.targetFps)) {
            "FPS نامعتبر: ${profile.targetFps}"
        }
        val keys = GameProfileKeys(profile.id.name)
        dataStore.edit { prefs ->
            prefs[keys.targetFps] = profile.targetFps
            prefs[keys.frameSmoothness] = profile.frameSmoothness.name
            prefs[keys.antiLag] = profile.antiLagEnabled
            prefs[keys.gpuAcceleration] = profile.gpuAccelerationEnabled
            prefs[keys.networkOptimization] = profile.networkOptimizationEnabled
            prefs[keys.overlay] = profile.overlayEnabled
            prefs[keys.crosshair] = profile.crosshairEnabled
            prefs[keys.batterySaver] = profile.batterySaverEnabled
        }
    }

    override suspend fun resetProfile(gameId: GameId) {
        updateProfile(GameProfile.default(gameId))
    }

    override suspend fun resetAllProfiles() {
        GameId.values().forEach { resetProfile(it) }
    }
}
