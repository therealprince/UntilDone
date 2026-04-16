package com.example.untildone.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.untildone.data.Journey
import com.example.untildone.ui.components.JourneyCard
import com.example.untildone.ui.theme.Emerald500
import com.example.untildone.ui.theme.UntilDoneTheme

@Composable
fun MissionsScreen(
    journeys: List<Journey>,
    onNavigate: (String, Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = UntilDoneTheme.colors
    var showActive by remember { mutableStateOf(true) }

    val activeJourneys = journeys.filter { !it.isComplete }
    val completedJourneys = journeys.filter { it.isComplete }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .padding(bottom = 80.dp)
    ) {
        // Header
        Text(
            text = "Missions",
            style = MaterialTheme.typography.headlineLarge,
            color = colors.textPrimary,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${activeJourneys.size} active · ${completedJourneys.size} completed",
            style = MaterialTheme.typography.bodySmall,
            color = colors.textTertiary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tab toggle: Active / Completed
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(colors.inputBackground)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (showActive) colors.cardBackground else Color.Transparent)
                    .clickable { showActive = true }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Active (${activeJourneys.size})",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (showActive) colors.textPrimary else colors.textTertiary
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (!showActive) colors.cardBackground else Color.Transparent)
                    .clickable { showActive = false }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Completed (${completedJourneys.size})",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (!showActive) colors.textPrimary else colors.textTertiary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (showActive) {
            // Active missions
            if (activeJourneys.isEmpty()) {
                EmptyState(
                    title = "No active missions",
                    subtitle = "Tap + to start a new mission"
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    activeJourneys.forEach { journey ->
                        JourneyCard(
                            journey = journey,
                            onClick = { onNavigate("detail", journey.id) }
                        )
                    }
                }
            }
        } else {
            // Completed missions
            if (completedJourneys.isEmpty()) {
                EmptyState(
                    title = "No completed missions yet",
                    subtitle = "Keep pushing — you'll get there"
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    completedJourneys.forEach { journey ->
                        // Completed mission card with badge
                        Box {
                            JourneyCard(
                                journey = journey,
                                onClick = { onNavigate("detail", journey.id) }
                            )
                            // Completion badge
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(top = 12.dp, end = 12.dp)
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Emerald500.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Completed",
                                    tint = Emerald500,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(title: String, subtitle: String) {
    val colors = UntilDoneTheme.colors
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.cardBackground)
            .padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = colors.textSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textTertiary
            )
        }
    }
}
