package com.prgamebooster.data.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

/** کلیدهای Jetpack DataStore. تمام تنظیمات و پروفایل‌های بازی از این طریق ماندگار می‌شوند. */
object SettingsKeys {
    val FLOATING_PANEL_ENABLED = booleanPreferencesKey("floating_panel_enabled")
    val HAPTIC_ENABLED = booleanPreferencesKey("haptic_enabled")
    val AUTO_REFRESH_ENABLED = booleanPreferencesKey("auto_refresh_enabled")
    val FULLSCREEN_ENABLED = booleanPreferencesKey("fullscreen_enabled")
    val TEXT_SCALE = floatPreferencesKey("text_scale")
    val ANIMATION_INTENSITY = stringPreferencesKey("animation_intensity")
    val ACTIVE_GAME_ID = stringPreferencesKey("active_game_id")
    val GAME_MODE_ACTIVE = booleanPreferencesKey("game_mode_active")
}

/** برای هر بازی یک مجموعه کلید مجزا با پیشوند packageId، تا پروفایل‌ها کاملاً مستقل ذخیره شوند. */
class GameProfileKeys(gameId: String) {
    val targetFps = intPreferencesKey("${gameId}_target_fps")
    val frameSmoothness = stringPreferencesKey("${gameId}_frame_smoothness")
    val antiLag = booleanPreferencesKey("${gameId}_anti_lag")
    val gpuAcceleration = booleanPreferencesKey("${gameId}_gpu_acceleration")
    val networkOptimization = booleanPreferencesKey("${gameId}_network_optimization")
    val overlay = booleanPreferencesKey("${gameId}_overlay")
    val crosshair = booleanPreferencesKey("${gameId}_crosshair")
    val batterySaver = booleanPreferencesKey("${gameId}_battery_saver")
}
