package com.prgamebooster.domain.model

data class AppSettings(
    val floatingPanelEnabled: Boolean = false,
    val hapticFeedbackEnabled: Boolean = true,
    val autoRefreshEnabled: Boolean = true,
    val fullscreenModeEnabled: Boolean = false,
    val textScale: Float = 1.0f,
    val animationIntensity: AnimationIntensity = AnimationIntensity.NORMAL,
    val activeGameId: GameId = GameId.PUBG_MOBILE,
    val gameModeActive: Boolean = false
)

enum class AnimationIntensity { OFF, REDUCED, NORMAL }
