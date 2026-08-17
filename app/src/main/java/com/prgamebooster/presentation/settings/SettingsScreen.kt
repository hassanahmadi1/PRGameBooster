package com.prgamebooster.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.prgamebooster.R
import com.prgamebooster.core.theme.*
import com.prgamebooster.domain.model.GameId
import com.prgamebooster.presentation.components.PRCard

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val settings by viewModel.settings.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.settings_title),
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        SectionLabel(stringResource(id = R.string.settings_general))
        Spacer(modifier = Modifier.height(8.dp))

        SettingRow("پنل شناور", settings.floatingPanelEnabled, viewModel::setFloatingPanel)
        SettingRow("بازخورد لرزشی", settings.hapticFeedbackEnabled, viewModel::setHaptic)
        SettingRow("به‌روزرسانی خودکار", settings.autoRefreshEnabled, viewModel::setAutoRefresh)
        SettingRow("حالت تمام‌صفحه", settings.fullscreenModeEnabled, viewModel::setFullscreen)

        Spacer(modifier = Modifier.height(20.dp))
        SectionLabel(stringResource(id = R.string.settings_profiles))
        Spacer(modifier = Modifier.height(8.dp))

        listOf(
            GameId.PUBG_MOBILE to "PUBG Mobile",
            GameId.FREE_FIRE to "Free Fire",
            GameId.COD_MOBILE to "Call of Duty Mobile"
        ).forEach { (id, label) ->
            PRCard(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = label, color = TextPrimary, fontSize = 14.sp)
                    TextButton(onClick = { viewModel.resetProfile(id) }) {
                        Text(text = "بازنشانی پروفایل", color = StatusInfo, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { showResetDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = StatusError, contentColor = TextPrimary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.settings_reset))
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(text = stringResource(id = R.string.settings_reset_confirm_title), color = TextPrimary) },
            text = { Text(text = stringResource(id = R.string.settings_reset_confirm_body), color = TextSecondary) },
            containerColor = CardColor,
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetAll()
                    showResetDialog = false
                }) {
                    Text(text = stringResource(id = R.string.settings_reset_action), color = StatusError)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(text = stringResource(id = R.string.settings_cancel), color = TextSecondary)
                }
            }
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text = text, color = PrimaryGold, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
}

@Composable
private fun SettingRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    PRCard(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, color = TextPrimary, fontSize = 14.sp)
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = PrimaryGold,
                    checkedTrackColor = CardActiveColor,
                    uncheckedThumbColor = TextSecondary
                )
            )
        }
    }
}
