package com.example.untildone.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.untildone.ui.theme.UntilDoneTheme

@Composable
fun SettingsScreen(
    userName: String,
    userEmail: String,
    profileImagePath: String,
    onChangeProfilePicture: () -> Unit,
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    onLogout: () -> Unit,
    onResetAccount: () -> Unit,
    onDeleteAccount: () -> Unit,
    onCreateBackup: () -> Unit,
    onRestoreBackup: () -> Unit,
    isPeriodicBackupEnabled: Boolean,
    onTogglePeriodicBackup: (Boolean) -> Unit,
    backupIntervalHours: Int,
    onSetBackupInterval: (Int) -> Unit,
    lastBackupInfo: String?,
    backupMessage: String?,
    modifier: Modifier = Modifier
) {
    val colors = UntilDoneTheme.colors
    val userInitial = remember(userName) {
        userName.firstOrNull()?.uppercase() ?: "U"
    }
    val profileBitmap = remember(profileImagePath) {
        if (profileImagePath.isNotBlank()) {
            try { BitmapFactory.decodeFile(profileImagePath) }
            catch (e: Exception) { null }
        } else null
    }

    // Confirmation dialogs
    var showResetDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Reset Account Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.RestartAlt,
                    contentDescription = null,
                    tint = colors.destructive,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Reset Account?",
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "This will permanently delete all your missions, focus sessions, and daily logs. Your account and login credentials will be kept.\n\nThis action cannot be undone.",
                    color = colors.textSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onResetAccount()
                    showResetDialog = false
                }) {
                    Text("Reset Everything", color = colors.destructive, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = colors.textTertiary)
                }
            },
            containerColor = colors.elevatedSurface
        )
    }

    // Delete Account Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.DeleteForever,
                    contentDescription = null,
                    tint = colors.destructive,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Delete Account?",
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "This will permanently delete your account and ALL associated data including missions, focus sessions, and logs.\n\nYou will be logged out and this action cannot be undone.",
                    color = colors.textSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteAccount()
                    showDeleteDialog = false
                }) {
                    Text("Delete Account", color = colors.destructive, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = colors.textTertiary)
                }
            },
            containerColor = colors.elevatedSurface
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .padding(bottom = 80.dp)
    ) {
        // Header
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge,
            color = colors.textPrimary,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
        )

        // Profile Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.cardBackground)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clickable(onClick = onChangeProfilePicture),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(colors.profileBackground),
                    contentAlignment = Alignment.Center
                ) {
                    if (profileBitmap != null) {
                        Image(
                            bitmap = profileBitmap.asImageBitmap(),
                            contentDescription = "Profile",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = userInitial,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.profileContent
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(colors.buttonPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CameraAlt,
                        contentDescription = "Change Photo",
                        tint = colors.buttonPrimaryContent,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = userName,
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = userEmail,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textTertiary
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Appearance Section
        SectionLabel("APPEARANCE")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.cardBackground)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Outlined.DarkMode
                    else Icons.Outlined.LightMode,
                    contentDescription = null,
                    tint = colors.textSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (isDarkMode) "Dark Mode" else "Light Mode",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
            Switch(
                checked = isDarkMode,
                onCheckedChange = { onToggleTheme() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = colors.buttonPrimary,
                    checkedTrackColor = colors.buttonPrimary.copy(alpha = 0.3f),
                    uncheckedThumbColor = colors.textTertiary,
                    uncheckedTrackColor = colors.inputBackground
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Backup & Restore Section
        SectionLabel("BACKUP & RESTORE")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.cardBackground)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SettingsButton(
                icon = Icons.Outlined.Backup,
                text = "Create Backup Now",
                onClick = onCreateBackup
            )

            SettingsButton(
                icon = Icons.Outlined.Restore,
                text = "Restore from Latest Backup",
                onClick = onRestoreBackup
            )

            if (lastBackupInfo != null) {
                Text(
                    text = lastBackupInfo,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textTertiary,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            if (backupMessage != null) {
                Text(
                    text = backupMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.emerald,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = colors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Automatic Backup",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
                Switch(
                    checked = isPeriodicBackupEnabled,
                    onCheckedChange = onTogglePeriodicBackup,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colors.buttonPrimary,
                        checkedTrackColor = colors.buttonPrimary.copy(alpha = 0.3f),
                        uncheckedThumbColor = colors.textTertiary,
                        uncheckedTrackColor = colors.inputBackground
                    )
                )
            }

            if (isPeriodicBackupEnabled) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(colors.inputBackground)
                        .padding(4.dp)
                ) {
                    FrequencyChip(
                        text = "Daily",
                        isSelected = backupIntervalHours == 24,
                        onClick = { onSetBackupInterval(24) },
                        modifier = Modifier.weight(1f)
                    )
                    FrequencyChip(
                        text = "Weekly",
                        isSelected = backupIntervalHours == 168,
                        onClick = { onSetBackupInterval(168) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Danger Zone
        SectionLabel("DANGER ZONE")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.cardBackground)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Reset Account
            DangerButton(
                icon = Icons.Outlined.RestartAlt,
                text = "Reset Account",
                subtitle = "Delete all data, keep account",
                onClick = { showResetDialog = true }
            )

            // Delete Account
            DangerButton(
                icon = Icons.Outlined.DeleteForever,
                text = "Delete Account",
                subtitle = "Permanently remove everything",
                onClick = { showDeleteDialog = true }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Log Out
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(colors.destructiveSurface)
                .clickable(onClick = onLogout)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Logout,
                    contentDescription = null,
                    tint = colors.destructive,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Log Out",
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.destructive
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "UntilDone v1.0",
            style = MaterialTheme.typography.bodySmall,
            color = colors.textTertiary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    val colors = UntilDoneTheme.colors
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(
            letterSpacing = 1.5.sp,
            fontSize = 10.sp
        ),
        color = colors.textTertiary,
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
    )
}

@Composable
private fun SettingsButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    val colors = UntilDoneTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(colors.inputBackground)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.textSecondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textPrimary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun DangerButton(
    icon: ImageVector,
    text: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val colors = UntilDoneTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(colors.cardBackground)
            .border(
                width = 1.dp,
                color = colors.destructive.copy(alpha = 0.3f),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colors.destructive.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.destructive,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textTertiary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun FrequencyChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = UntilDoneTheme.colors
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) colors.cardBackground else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (isSelected) colors.textPrimary else colors.textTertiary,
            fontSize = 12.sp
        )
    }
}
