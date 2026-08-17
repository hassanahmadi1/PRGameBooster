package com.prgamebooster.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/** یک DataStore واحد برای کل اپ؛ هم تنظیمات عمومی و هم پروفایل هر بازی در آن نگه‌داری می‌شود. */
val Context.prGameBoosterDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "pr_game_booster_settings"
)
