package com.example.untildone.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.untildone.ui.theme.Emerald500
import com.example.untildone.ui.theme.Neutral400
import com.example.untildone.ui.theme.Neutral500
import com.example.untildone.ui.theme.Neutral800
import com.example.untildone.ui.theme.Neutral900
import com.example.untildone.ui.theme.Neutral950
import kotlinx.coroutines.delay

@Composable
fun FocusTimerScreen(
    onBack: () -> Unit,
    onSessionComplete: (durationSeconds: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalTime = 25 * 60
    var timeLeft by remember { mutableIntStateOf(totalTime) }
    var isActive by remember { mutableStateOf(false) }
    var sessionCompleted by remember { mutableStateOf(false) }

    // Timer countdown
    LaunchedEffect(isActive) {
        while (isActive && timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
        if (timeLeft == 0 && !sessionCompleted) {
            isActive = false
            sessionCompleted = true
            onSessionComplete(totalTime)
        }
    }

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val progress = (totalTime - timeLeft).toFloat() / totalTime

    // Animated glow color
    val glowColor by animateColorAsState(
        targetValue = if (isActive) Color(0x33059669) else Color(0x1A262626),
        animationSpec = tween(1000),
        label = "glow"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Neutral950)
    ) {
        // Background glow
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.Center)
                .blur(80.dp)
                .clip(CircleShape)
                .background(glowColor)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Neutral900)
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Back",
                        tint = Neutral400,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "DEEP WORK",
                    style = MaterialTheme.typography.labelMedium,
                    color = Neutral500,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.size(40.dp))
            }

            // Timer area
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Circular progress
                Box(
                    modifier = Modifier.size(256.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 6.dp.toPx()
                        val padding = strokeWidth / 2
                        val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)

                        // Background circle
                        drawArc(
                            color = Neutral800,
                            startAngle = -90f,
                            sweepAngle = 360f,
                            useCenter = false,
                            topLeft = Offset(padding, padding),
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        // Progress arc
                        drawArc(
                            color = Emerald500,
                            startAngle = -90f,
                            sweepAngle = 360f * progress,
                            useCenter = false,
                            topLeft = Offset(padding, padding),
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    // Time display
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${minutes.toString().padStart(2, '0')}:${
                                seconds.toString().padStart(2, '0')
                            }",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Light,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White,
                            letterSpacing = (-2).sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (sessionCompleted) "COMPLETE"
                            else if (isActive) "FOCUSING" else "PAUSED",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (sessionCompleted) Emerald500
                            else if (isActive) Emerald500 else Neutral500,
                            letterSpacing = 2.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Controls
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Reset
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Neutral900)
                            .clickable {
                                isActive = false
                                timeLeft = totalTime
                                sessionCompleted = false
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset",
                            tint = Neutral400,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Play/Pause
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(if (isActive) Neutral800 else Color.White)
                            .clickable {
                                if (!sessionCompleted) {
                                    isActive = !isActive
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isActive) Icons.Default.Pause
                            else Icons.Default.PlayArrow,
                            contentDescription = if (isActive) "Pause" else "Play",
                            tint = if (isActive) Color.White else Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Quote and DND badge
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "\"Focus on the step in front of you.\"",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontStyle = FontStyle.Italic
                        ),
                        color = Neutral500,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Neutral900.copy(alpha = 0.5f))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DarkMode,
                            contentDescription = null,
                            tint = Neutral400,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Do Not Disturb Recommended",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Neutral400
                        )
                    }
                }
            }
        }
    }
}
