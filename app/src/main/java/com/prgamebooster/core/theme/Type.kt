package com.prgamebooster.core.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * فونت پیشنهادی پروژه Vazirmatn است.
 * برای فعال‌سازی: فایل‌های وزیرمتن (Regular/Medium/Bold) را در res/font/ قرار بده
 * و FontFamily سفارشی را جایگزین FontFamily.SansSerif کن.
 * تا آن زمان، سیستم از فونت پیش‌فرض سیستم برای فارسی استفاده می‌کند.
 */
val VazirmatnFontFamily = FontFamily.SansSerif

val PRGameBoosterTypography = Typography(
    headlineMedium = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    titleLarge = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ),
    titleMedium = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp
    ),
    labelSmall = TextStyle(
        fontFamily = VazirmatnFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp
    )
)
