package com.therealprince.untildone.ui.components

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.therealprince.untildone.data.ReleaseInfo
import com.therealprince.untildone.data.UpdateManager
import com.therealprince.untildone.ui.theme.UntilDoneTheme
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

private enum class UpdatePhase { Prompt, Downloading, Ready, Error }

@Composable
fun UpdateDialog(
    release: ReleaseInfo,
    currentVersion: String,
    onSkip: () -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val colors = UntilDoneTheme.colors

    var phase by remember { mutableStateOf(UpdatePhase.Prompt) }
    var downloaded by remember { mutableLongStateOf(0L) }
    var total by remember { mutableLongStateOf(release.sizeBytes) }
    var apkFile by remember { mutableStateOf<File?>(null) }
    var errorText by remember { mutableStateOf<String?>(null) }

    val installPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val file = apkFile
        if (file != null && UpdateManager.canInstallPackages(context)) {
            UpdateManager.installApk(context, file)
        }
    }

    fun launchInstall(file: File) {
        if (UpdateManager.canInstallPackages(context)) {
            UpdateManager.installApk(context, file)
        } else {
            installPermLauncher.launch(UpdateManager.installPermissionIntent(context))
        }
    }

    fun startDownload() {
        phase = UpdatePhase.Downloading
        downloaded = 0L
        total = release.sizeBytes
        errorText = null
        scope.launch {
            try {
                val file = UpdateManager.downloadApk(
                    context = context,
                    url = release.downloadUrl,
                    expectedSize = release.sizeBytes,
                ) { d, t ->
                    downloaded = d
                    if (t > 0) total = t
                }
                apkFile = file
                phase = UpdatePhase.Ready
                launchInstall(file)
            } catch (e: Exception) {
                errorText = e.message ?: "Download failed"
                phase = UpdatePhase.Error
            }
        }
    }

    LaunchedEffect(release.version) {
        phase = UpdatePhase.Prompt
    }

    AlertDialog(
        onDismissRequest = {
            if (phase != UpdatePhase.Downloading) onDismiss()
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.NewReleases,
                contentDescription = null,
                tint = colors.emerald,
                modifier = Modifier.size(32.dp),
            )
        },
        title = {
            Text(
                text = "Update available",
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
            )
        },
        text = {
            Column {
                Text(
                    text = "${release.name}  •  v${release.version}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.size(2.dp))
                Text(
                    text = "You have v$currentVersion",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textTertiary,
                )

                if (release.body.isNotBlank() && phase == UpdatePhase.Prompt) {
                    Spacer(Modifier.size(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 180.dp)
                            .background(colors.inputBackground, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState()),
                    ) {
                        Text(
                            text = release.body.trim(),
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary,
                            fontSize = 12.sp,
                        )
                    }
                }

                when (phase) {
                    UpdatePhase.Downloading -> {
                        Spacer(Modifier.size(16.dp))
                        val fraction = if (total > 0) {
                            (downloaded.toFloat() / total).coerceIn(0f, 1f)
                        } else 0f
                        LinearProgressIndicator(
                            progress = { fraction },
                            modifier = Modifier.fillMaxWidth(),
                            color = colors.emerald,
                            trackColor = colors.inputBackground,
                        )
                        Spacer(Modifier.size(8.dp))
                        Text(
                            text = "Downloading… ${formatBytes(downloaded)} / ${formatBytes(total)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textTertiary,
                        )
                    }
                    UpdatePhase.Ready -> {
                        Spacer(Modifier.size(12.dp))
                        Text(
                            text = "Download complete. Tap Install to finish.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.emerald,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    UpdatePhase.Error -> {
                        Spacer(Modifier.size(12.dp))
                        Text(
                            text = "Update failed: ${errorText ?: "unknown error"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.destructive,
                        )
                    }
                    else -> Unit
                }
            }
        },
        confirmButton = {
            when (phase) {
                UpdatePhase.Prompt -> TextButton(onClick = { startDownload() }) {
                    Text("Update", color = colors.emerald, fontWeight = FontWeight.Bold)
                }
                UpdatePhase.Downloading -> TextButton(
                    onClick = {},
                    enabled = false,
                ) {
                    Text("Downloading…", color = colors.textTertiary)
                }
                UpdatePhase.Ready -> TextButton(onClick = {
                    apkFile?.let { launchInstall(it) }
                }) {
                    Text("Install", color = colors.emerald, fontWeight = FontWeight.Bold)
                }
                UpdatePhase.Error -> TextButton(onClick = { startDownload() }) {
                    Text("Retry", color = colors.emerald, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            when (phase) {
                UpdatePhase.Prompt -> Row {
                    TextButton(onClick = onSkip) {
                        Text("Skip", color = colors.textTertiary)
                    }
                    TextButton(onClick = onDismiss) {
                        Text("Later", color = colors.textTertiary)
                    }
                }
                UpdatePhase.Downloading -> {}
                else -> TextButton(onClick = onDismiss) {
                    Text("Close", color = colors.textTertiary)
                }
            }
        },
        containerColor = colors.elevatedSurface,
    )
}

private fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val mb = bytes / 1024.0 / 1024.0
    if (mb >= 1.0) return String.format(Locale.US, "%.1f MB", mb)
    val kb = bytes / 1024.0
    return String.format(Locale.US, "%.0f KB", kb)
}
