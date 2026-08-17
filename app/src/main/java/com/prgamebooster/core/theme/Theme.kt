package com.prgamebooster.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection

private val PRGameBoosterColorScheme = darkColorScheme(
    background = BackgroundPrimary,
    surface = CardColor,
    surfaceVariant = CardActiveColor,
    primary = PrimaryGold,
    onPrimary = BackgroundPrimary,
    secondary = PrimaryGoldLight,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = OutlineColor,
    error = StatusError
)

/**
 * تم اصلی اپ: Dark Gaming Premium + جهت RTL اجباری برای رابط کاربری فارسی.
 */
@Composable
fun PRGameBoosterTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = PRGameBoosterColorScheme,
            typography = PRGameBoosterTypography,
            content = content
        )
    }
}
