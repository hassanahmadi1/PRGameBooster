package com.prgamebooster.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Monitor : Screen("monitor")
    object Tools : Screen("tools")
    object Settings : Screen("settings")
}
