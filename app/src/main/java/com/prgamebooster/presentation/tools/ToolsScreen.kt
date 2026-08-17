package com.prgamebooster.presentation.tools

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.prgamebooster.R
import com.prgamebooster.core.theme.*
import com.prgamebooster.domain.model.FrameSmoothness
import com.prgamebooster.games.LaunchResult
import com.prgamebooster.presentation.components.PRCard

@Composable
fun ToolsScreen(viewModel: ToolsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.tools_title),
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        // نشانه‌گر (Crosshair) - نیازمند مجوز Overlay واقعی
        ToggleCard(
            title = stringResource(id = R.string.tools_crosshair),
            checked = state.activeProfile?.crosshairEnabled == true,
            onCheckedChange = { checked ->
                if (checked && !hasOverlayPermission(context)) {
                    Toast.makeText(context, context.getString(R.string.tools_overlay_permission_required), Toast.LENGTH_LONG).show()
                    requestOverlayPermission(context)
                } else {
                    viewModel.updateProfile { it.copy(crosshairEnabled = checked) }
                }
            }
        )

        // بازخورد لرزشی - واقعی از طریق Vibrator API
        ToggleCard(
            title = stringResource(id = R.string.tools_haptic),
            checked = state.settings.hapticFeedbackEnabled,
            enabled = state.hapticSupported,
            subtitle = if (!state.hapticSupported) stringResource(id = R.string.tools_haptic_unsupported) else null,
            onCheckedChange = { checked ->
                viewModel.setHaptic(checked)
                if (checked) viewModel.hapticController.tick()
            }
        )

        // پنل شناور
        ToggleCard(
            title = stringResource(id = R.string.tools_floating_panel),
            checked = state.settings.floatingPanelEnabled,
            onCheckedChange = { checked ->
                if (checked && !hasOverlayPermission(context)) {
                    Toast.makeText(context, context.getString(R.string.tools_overlay_permission_required), Toast.LENGTH_LONG).show()
                    requestOverlayPermission(context)
                } else {
                    viewModel.setFloatingPanel(checked)
                }
            }
        )

        // حالت تمام‌صفحه
        ToggleCard(
            title = stringResource(id = R.string.tools_fullscreen),
            checked = state.settings.fullscreenModeEnabled,
            onCheckedChange = { viewModel.setFullscreen(it) }
        )

        // کاهش تأخیر
        ToggleCard(
            title = stringResource(id = R.string.tools_anti_lag),
            checked = state.activeProfile?.antiLagEnabled == true,
            onCheckedChange = { checked -> viewModel.updateProfile { it.copy(antiLagEnabled = checked) } }
        )

        // شتاب‌دهی گرافیکی رابط برنامه
        ToggleCard(
            title = stringResource(id = R.string.tools_gpu_accel),
            checked = state.activeProfile?.gpuAccelerationEnabled == true,
            onCheckedChange = { checked -> viewModel.updateProfile { it.copy(gpuAccelerationEnabled = checked) } }
        )

        // صرفه‌جویی باتری
        ToggleCard(
            title = stringResource(id = R.string.tools_battery_saver),
            checked = state.activeProfile?.batterySaverEnabled == true,
            onCheckedChange = { checked -> viewModel.updateProfile { it.copy(batterySaverEnabled = checked) } }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // هدف FPS
        PRCard(modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(id = R.string.tools_fps_target), color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = stringResource(id = R.string.tools_fps_note), color = TextSecondary, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(12.dp))
            val fpsOptions = com.prgamebooster.domain.model.GameProfile.ALLOWED_FPS_VALUES
            val currentFps = state.activeProfile?.targetFps ?: 60
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                fpsOptions.forEach { fps ->
                    FilterChip(
                        selected = fps == currentFps,
                        onClick = { viewModel.updateProfile { it.copy(targetFps = fps) } },
                        label = { Text(text = fps.toString(), fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryGold,
                            selectedLabelColor = BackgroundPrimary,
                            containerColor = CardActiveColor,
                            labelColor = TextSecondary
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // روانی تصویر
        PRCard(modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(id = R.string.tools_frame_smoothness), color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FrameSmoothness.values().forEach { mode ->
                    FilterChip(
                        selected = state.activeProfile?.frameSmoothness == mode,
                        onClick = { viewModel.updateProfile { it.copy(frameSmoothness = mode) } },
                        label = { Text(text = frameSmoothnessLabel(mode), fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryGold,
                            selectedLabelColor = BackgroundPrimary,
                            containerColor = CardActiveColor,
                            labelColor = TextSecondary
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // اجرای بازی
        state.activeProfile?.let { profile ->
            PRCard(modifier = Modifier.fillMaxWidth()) {
                Text(text = profile.displayName, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        when (viewModel.launchGame(profile.packageName)) {
                            LaunchResult.Launched -> Unit
                            LaunchResult.NotInstalled -> {
                                Toast.makeText(context, context.getString(R.string.tools_game_not_installed), Toast.LENGTH_LONG).show()
                                viewModel.gameLauncher.openInStore(context, profile.packageName)
                            }
                            LaunchResult.LaunchFailed -> Toast.makeText(context, "اجرای بازی ناموفق بود", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGold, contentColor = BackgroundPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(id = R.string.tools_launch_game))
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ToggleCard(
    title: String,
    checked: Boolean,
    enabled: Boolean = true,
    subtitle: String? = null,
    onCheckedChange: (Boolean) -> Unit
) {
    PRCard(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = TextPrimary, fontSize = 14.sp)
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = subtitle, color = TextSecondary, fontSize = 11.sp)
                }
            }
            Switch(
                checked = checked,
                enabled = enabled,
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

private fun frameSmoothnessLabel(mode: FrameSmoothness): String = when (mode) {
    FrameSmoothness.LOW -> "کم"
    FrameSmoothness.MEDIUM -> "متوسط"
    FrameSmoothness.ULTRA -> "اولترا"
}

private fun hasOverlayPermission(context: android.content.Context): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Settings.canDrawOverlays(context) else true

private fun requestOverlayPermission(context: android.content.Context) {
    val intent = Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        Uri.parse("package:${context.packageName}")
    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}
