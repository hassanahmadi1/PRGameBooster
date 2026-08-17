package com.prgamebooster.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import android.widget.Toast
import com.prgamebooster.R
import com.prgamebooster.core.theme.*
import com.prgamebooster.domain.model.BatteryState
import com.prgamebooster.domain.model.GameId
import com.prgamebooster.domain.model.GameProfile
import com.prgamebooster.domain.model.NetworkState
import com.prgamebooster.presentation.components.MetricCard
import com.prgamebooster.presentation.components.PRCard
import com.prgamebooster.presentation.components.PRPrimaryButton
import com.prgamebooster.presentation.components.StatusDot

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        HomeHeader()
        Spacer(modifier = Modifier.height(20.dp))

        ActiveGameCard(state)
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "انتخاب بازی",
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        GameSelectionRow(state.profiles, state.activeGameId) { gameId ->
            viewModel.selectGame(gameId)
            val name = state.profiles.find { it.id == gameId }?.displayName ?: ""
            Toast.makeText(
                context,
                context.getString(R.string.toast_profile_selected, name),
                Toast.LENGTH_SHORT
            ).show()
        }

        Spacer(modifier = Modifier.height(20.dp))
        MetricsGrid(state)

        Spacer(modifier = Modifier.height(24.dp))

        if (state.boostInProgress) {
            val stepText = state.boostStepMessageResId?.let { stringResource(id = it) } ?: ""
            PRCard(modifier = Modifier.fillMaxWidth()) {
                CircularProgressIndicator(color = PrimaryGold, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = stepText, color = TextSecondary, fontSize = 13.sp)
            }
        } else {
            PRPrimaryButton(
                text = if (state.gameModeActive)
                    stringResource(id = R.string.home_status_active)
                else
                    stringResource(id = R.string.home_activate_button),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (state.gameModeActive) viewModel.deactivateGameMode()
                    else viewModel.activateGameMode()
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun HomeHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Bolt, contentDescription = null, tint = PrimaryGold)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(id = R.string.app_name),
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            Text(
                text = stringResource(id = R.string.home_subtitle),
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
        IconButton(onClick = { /* باز کردن تنظیمات سریع در نسخه‌های بعدی از همین صفحه */ }) {
            Icon(Icons.Filled.Settings, contentDescription = null, tint = TextSecondary)
        }
    }
}

@Composable
private fun ActiveGameCard(state: HomeUiState) {
    val profile = state.activeProfile
    PRCard(modifier = Modifier.fillMaxWidth(), active = true) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.home_active_game),
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Text(
                    text = profile?.displayName ?: "-",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            StatusDot(
                color = if (state.gameModeActive) StatusSuccess else StatusInfo,
                label = if (state.gameModeActive)
                    stringResource(id = R.string.home_status_active)
                else
                    stringResource(id = R.string.home_status_ready)
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = "${stringResource(id = R.string.monitor_fps_target)}: ${profile?.targetFps ?: "-"}",
                color = TextSecondary,
                fontSize = 12.sp
            )
            Text(
                text = if (state.gameModeActive) "Performance Mode: ON" else "Performance Mode: OFF",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun GameSelectionRow(
    profiles: List<GameProfile>,
    activeGameId: GameId,
    onSelect: (GameId) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(profiles, key = { it.id.name }) { profile ->
            val isActive = profile.id == activeGameId
            PRCard(
                modifier = Modifier
                    .width(140.dp)
                    .clickableCard { onSelect(profile.id) },
                active = isActive
            ) {
                Text(text = profile.displayName, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${profile.targetFps} FPS", color = TextSecondary, fontSize = 11.sp)
            }
        }
    }
}

private fun Modifier.clickableCard(onClick: () -> Unit): Modifier =
    this.then(androidx.compose.foundation.clickable(onClick = onClick))

@Composable
private fun MetricsGrid(state: HomeUiState) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        val batteryText = when (val b = state.batteryState) {
            is BatteryState.Available -> "${b.percentage}%"
            BatteryState.Unavailable -> stringResource(id = R.string.monitor_not_available)
        }
        MetricCard(
            modifier = Modifier.weight(1f),
            title = stringResource(id = R.string.monitor_battery),
            value = batteryText,
            valueColor = if (state.batteryState is BatteryState.Available) StatusSuccess else TextSecondary
        )

        val networkText = when (val n = state.networkState) {
            is NetworkState.Connected -> n.type.name
            NetworkState.Disconnected -> stringResource(id = R.string.monitor_not_available)
        }
        MetricCard(
            modifier = Modifier.weight(1f),
            title = stringResource(id = R.string.monitor_network),
            value = networkText,
            valueColor = StatusInfo
        )
    }
}
