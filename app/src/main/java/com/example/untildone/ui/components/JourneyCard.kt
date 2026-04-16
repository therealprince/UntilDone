package com.example.untildone.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.untildone.data.Journey
import com.example.untildone.ui.theme.UntilDoneTheme

@Composable
fun JourneyCard(
    journey: Journey,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = UntilDoneTheme.colors

    // Animate progress bar
    var targetProgress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 1000),
        label = "progress"
    )
    LaunchedEffect(journey.percentage) {
        targetProgress = journey.percentage / 100f
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.cardBackground)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        // Tag + completion badge
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            // Tag
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(colors.tagBackground)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = journey.tag,
                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                    color = colors.tagText,
                    letterSpacing = 1.sp
                )
            }

            if (journey.isComplete) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Complete",
                    tint = colors.emeraldLight,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Title
        Text(
            text = journey.title,
            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
            color = colors.textPrimary,
            modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
        )

        // Progress info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${journey.percentage}%",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.textSecondary
            )
            Text(
                text = "${journey.progress} / ${journey.target} ${journey.unit}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = colors.textTertiary
            )
        }

        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(colors.progressTrack)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress.coerceIn(0f, 1f))
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (journey.isComplete) colors.progressComplete
                        else colors.progressFill
                    )
            )
        }
    }
}
