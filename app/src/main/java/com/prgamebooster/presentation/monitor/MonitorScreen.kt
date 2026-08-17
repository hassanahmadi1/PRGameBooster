package com.prgamebooster.presentation.monitor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.prgamebooster.R
import com.prgamebooster.core.theme.*
import com.prgamebooster.domain.model.*
import com.prgamebooster.presentation.components.MetricCard
import com.prgamebooster.presentation.components.PRCard
import com.prgamebooster.presentation.components.PRPrimaryButton

@Composable
fun MonitorScreen(viewModel: MonitorViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.monitor_title),
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            val batteryValue: String
            val batterySubtitle: String?
            when (val b = state.batteryState) {
                is BatteryState.Available -> {
                    batteryValue = "${b.percentage}%"
                    batterySubtitle = if (b.isCharging) stringResource(id = R.string.monitor_charging)
                    else stringResource(id = R.string.monitor_not_charging)
                }
                BatteryState.Unavailable -> {
                    batteryValue = stringResource(id = R.string.monitor_battery_unavailable)
                    batterySubtitle = null
                }
            }
            MetricCard(
                modifier = Modifier.weight(1f),
                title = stringResource(id = R.string.monitor_battery),
                value = batteryValue,
                valueColor = if (state.batteryState is BatteryState.Available) StatusSuccess else TextSecondary,
                subtitle = batterySubtitle
            )

            val networkValue = when (val n = state.networkState) {
                is NetworkState.Connected -> networkTypeLabel(n.type)
                NetworkState.Disconnected -> stringResource(id = R.string.monitor_not_available)
            }
            MetricCard(
                modifier = Modifier.weight(1f),
                title = stringResource(id = R.string.monitor_network),
                value = networkValue,
                valueColor = StatusInfo
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            val speedText = state.downstreamMbps?.let { "%.1f Mbps".format(it) }
                ?: stringResource(id = R.string.monitor_not_available)
            MetricCard(
                modifier = Modifier.weight(1f),
                title = stringResource(id = R.string.monitor_speed),
                value = speedText,
                valueColor = StatusPerformance
            )

            val latencyText = when (val l = state.latencyResult) {
                is LatencyResult.Measured -> "${l.millis} ms"
                LatencyResult.Unavailable -> stringResource(id = R.string.monitor_not_available)
                LatencyResult.NotTestedYet -> "—"
            }
            MetricCard(
                modifier = Modifier.weight(1f),
                title = stringResource(id = R.string.monitor_latency),
                value = latencyText,
                valueColor = StatusInfo
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        PRCard(modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(id = R.string.monitor_fps_target), color = TextSecondary, fontSize = 12.sp)
            Text(
                text = "${state.targetFps} FPS",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = stringResource(id = R.string.monitor_fps_actual), color = TextSecondary, fontSize = 12.sp)
            val actualText = when (val f = state.actualFps) {
                is FpsReading.Actual -> "${f.fps} FPS"
                FpsReading.Unavailable -> stringResource(id = R.string.monitor_not_available)
            }
            Text(text = actualText, color = TextSecondary, fontWeight = FontWeight.Medium, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (state.isTestingLatency) {
            PRCard(modifier = Modifier.fillMaxWidth()) {
                CircularProgressIndicator(color = PrimaryGold, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = stringResource(id = R.string.monitor_testing), color = TextSecondary, fontSize = 13.sp)
            }
        } else {
            PRPrimaryButton(
                text = stringResource(id = R.string.monitor_test_connection),
                modifier = Modifier.fillMaxWidth(),
                onClick = { viewModel.runLatencyTest() }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

private fun networkTypeLabel(type: NetworkType): String = when (type) {
    NetworkType.WIFI -> "Wi-Fi"
    NetworkType.CELLULAR_5G -> "5G"
    NetworkType.CELLULAR_4G -> "4G"
    NetworkType.CELLULAR_OTHER -> "شبکه موبایل"
    NetworkType.ETHERNET -> "Ethernet"
    NetworkType.UNKNOWN -> "نامشخص"
}
