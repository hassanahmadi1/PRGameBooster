package com.prgamebooster.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.prgamebooster.presentation.home.HomeScreen
import com.prgamebooster.presentation.monitor.MonitorScreen
import com.prgamebooster.presentation.settings.SettingsScreen
import com.prgamebooster.presentation.tools.ToolsScreen

private data class BottomNavItem(
    val screen: Screen,
    val labelResName: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, "nav_home", Icons.Filled.Home),
    BottomNavItem(Screen.Monitor, "nav_monitor", Icons.Filled.Speed),
    BottomNavItem(Screen.Tools, "nav_tools", Icons.Filled.Build),
    BottomNavItem(Screen.Settings, "nav_settings", Icons.Filled.Settings)
)

@Composable
fun PRGameBoosterNavHost() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { PRGameBoosterBottomBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Monitor.route) { MonitorScreen() }
            composable(Screen.Tools.route) { ToolsScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}

@Composable
private fun PRGameBoosterBottomBar(navController: androidx.navigation.NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface) {
        bottomNavItems.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true
            val label = stringResourceByName(item.labelResName)

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = label) },
                label = { Text(label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = com.prgamebooster.core.theme.PrimaryGold,
                    selectedTextColor = com.prgamebooster.core.theme.PrimaryGold,
                    unselectedIconColor = com.prgamebooster.core.theme.TextSecondary,
                    unselectedTextColor = com.prgamebooster.core.theme.TextSecondary,
                    indicatorColor = com.prgamebooster.core.theme.CardActiveColor
                )
            )
        }
    }
}

@Composable
private fun stringResourceByName(name: String): String {
    val context = androidx.compose.ui.platform.LocalContext.current
    val resId = context.resources.getIdentifier(name, "string", context.packageName)
    return if (resId != 0) androidx.compose.ui.res.stringResource(id = resId) else name
}
