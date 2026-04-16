package com.example.untildone.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.untildone.data.Journey
import com.example.untildone.ui.theme.UntilDoneTheme

@Composable
fun JourneyDetailScreen(
    journey: Journey?,
    onBack: () -> Unit,
    onProgress: (Long, Int) -> Unit,
    onGiveUp: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (journey == null) return

    val colors = UntilDoneTheme.colors
    var logAmount by remember(journey.id) { mutableIntStateOf(journey.dailyTarget.coerceAtLeast(1)) }

    // Animate progress
    var targetProgress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 700),
        label = "detailProgress"
    )
    LaunchedEffect(journey.percentage) {
        targetProgress = journey.percentage / 100f
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Sticky header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.background.copy(alpha = 0.8f))
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(colors.cardBackground)
                    .clickable(onClick = onBack),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Back",
                    tint = colors.textSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            Text(
                text = "MISSION STATUS",
                style = MaterialTheme.typography.labelMedium,
                color = colors.textTertiary,
                letterSpacing = 2.sp
            )

            // Spacer for centering
            Spacer(modifier = Modifier.size(36.dp))
        }

        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 12.dp, bottom = 80.dp)
        ) {
            // Tag
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(colors.tagBackground)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = journey.tag,
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.5.sp),
                    color = colors.tagText
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Title
            Text(
                text = journey.title,
                style = MaterialTheme.typography.headlineLarge,
                color = colors.textPrimary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Large Progress Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(colors.cardBackground)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${journey.percentage}%",
                    style = MaterialTheme.typography.displayLarge,
                    color = colors.textPrimary
                )
                Text(
                    text = "Completed",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textTertiary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Progress info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${journey.progress} ${journey.unit}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary
                    )
                    Text(
                        text = "${journey.target} ${journey.unit}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textSecondary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

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

            Spacer(modifier = Modifier.height(20.dp))

            // Check-in or Completion card
            if (!journey.isComplete) {
                // Log Progress Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(colors.cardBackground)
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Log Progress",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "How many ${journey.unit} did you complete?",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textTertiary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stepper
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.background)
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Minus button
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(colors.stepperBackground)
                                .clickable { logAmount = (logAmount - 1).coerceAtLeast(1) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease",
                                tint = colors.textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Value
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$logAmount",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black
                                ),
                                color = colors.textPrimary
                            )
                            Text(
                                text = journey.unit.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 2.sp
                                ),
                                color = colors.textTertiary
                            )
                        }

                        // Plus button
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(colors.stepperBackground)
                                .clickable { logAmount++ },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase",
                                tint = colors.textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Commit button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.emerald)
                            .clickable {
                                if (logAmount > 0) {
                                    onProgress(journey.id, logAmount)
                                    logAmount = journey.dailyTarget.coerceAtLeast(1)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = androidx.compose.ui.graphics.Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Commit Progress",
                                style = MaterialTheme.typography.labelLarge,
                                color = androidx.compose.ui.graphics.Color.White
                            )
                        }
                    }
                }
            } else {
                // Mission Accomplished Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(colors.emeraldSurface)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(colors.emeraldBorder),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Complete",
                            tint = colors.emerald,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Mission Accomplished",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.emeraldOnSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Execution is everything. Well done.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.emerald.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Stats grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Completed stat
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.cardBackground)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Completed",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.textTertiary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${journey.progress}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = colors.textPrimary
                    )
                }

                // Remaining stat
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(colors.cardBackground)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Remaining",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.textTertiary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${journey.remaining}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = colors.textPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Abandon Mission
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onGiveUp(journey.id) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Abandon Mission",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.destructive,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
