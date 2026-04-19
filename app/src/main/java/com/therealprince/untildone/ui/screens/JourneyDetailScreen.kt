package com.therealprince.untildone.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.therealprince.untildone.data.Journey
import com.therealprince.untildone.ui.theme.Emerald400
import com.therealprince.untildone.ui.theme.Emerald500
import com.therealprince.untildone.ui.theme.Emerald600
import com.therealprince.untildone.ui.theme.UntilDoneTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.ceil

@Composable
fun JourneyDetailScreen(
    journey: Journey?,
    onBack: () -> Unit,
    onProgress: (Long, Int) -> Unit,
    onGiveUp: (Long) -> Unit,
    onEdit: (Journey) -> Unit,
    modifier: Modifier = Modifier
) {
    if (journey == null) return

    val colors = UntilDoneTheme.colors
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    val todayCompleted = journey.todayCompletedFor(today)

    var logAmount by remember(journey.id) {
        mutableIntStateOf(journey.dailyTarget.coerceAtLeast(1))
    }
    var showAbandonDialog by remember { mutableStateOf(false) }
    var menuOpen by remember { mutableStateOf(false) }

    val screenHeight = LocalConfiguration.current.screenHeightDp
    val scale = (screenHeight / 780f).coerceIn(0.85f, 1.35f)

    var targetProgress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 700),
        label = "detailProgress"
    )
    LaunchedEffect(journey.percentage) {
        targetProgress = journey.percentage / 100f
    }

    val percentage = journey.percentage
    val remainingUnits = journey.remaining
    val isComplete = journey.isComplete
    val daysLeft = if (journey.dailyTarget > 0) {
        ceil(remainingUnits.toDouble() / journey.dailyTarget).toInt()
    } else remainingUnits

    val estDate = remember(daysLeft) {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, daysLeft)
        SimpleDateFormat("MMM dd, yyyy", Locale.US).format(cal.time)
    }

    val potentialTodayTotal = todayCompleted + logAmount
    val isMinReached = potentialTodayTotal >= journey.dailyTarget
    val isMaxReached = journey.dailyMax != null && potentialTodayTotal >= journey.dailyMax

    LaunchedEffect(remainingUnits) {
        if (logAmount > remainingUnits && remainingUnits > 0) logAmount = remainingUnits
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.background.copy(alpha = 0.8f))
                .padding(horizontal = 20.dp, vertical = (12 * scale).dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
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

            Box {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(colors.cardBackground)
                        .clickable { menuOpen = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = colors.textSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                DropdownMenu(
                    expanded = menuOpen,
                    onDismissRequest = { menuOpen = false },
                    modifier = Modifier.background(colors.dropdownBackground)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Edit Mission",
                                color = colors.textPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        onClick = {
                            menuOpen = false
                            onEdit(journey)
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Abandon",
                                color = colors.destructive,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        onClick = {
                            menuOpen = false
                            showAbandonDialog = true
                        }
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = (10 * scale).dp, bottom = (24 * scale).dp)
        ) {
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

            Spacer(modifier = Modifier.height((10 * scale).dp))

            Text(
                text = journey.title,
                style = MaterialTheme.typography.headlineLarge,
                color = colors.textPrimary
            )

            Spacer(modifier = Modifier.height((16 * scale).dp))

            HeroCard(
                isComplete = isComplete,
                daysLeft = daysLeft,
                percentage = percentage,
                journey = journey,
                estDate = estDate,
                animatedProgress = animatedProgress,
                scale = scale
            )

            Spacer(modifier = Modifier.height((16 * scale).dp))

            if (!isComplete) {
                LogProgressCard(
                    journey = journey,
                    todayCompleted = todayCompleted,
                    logAmount = logAmount,
                    onLogAmountChange = { logAmount = it.coerceIn(1, remainingUnits.coerceAtLeast(1)) },
                    isMinReached = isMinReached,
                    isMaxReached = isMaxReached,
                    remainingUnits = remainingUnits,
                    scale = scale,
                    onCommit = {
                        if (logAmount > 0) {
                            onProgress(journey.id, logAmount)
                            logAmount = journey.dailyTarget.coerceAtLeast(1)
                        }
                    }
                )
            } else {
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

            Spacer(modifier = Modifier.height((16 * scale).dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatTile(
                    label = "COMPLETED",
                    value = "${journey.progress}",
                    suffix = " / ${journey.target}",
                    scale = scale,
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    label = "REMAINING UNIT",
                    value = "$remainingUnits",
                    suffix = " ${journey.unit.replaceFirstChar { it.uppercase() }}",
                    scale = scale,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height((12 * scale).dp))

            FocusTimeCard(timeSpentSeconds = journey.timeSpent, scale = scale)
        }
    }

    if (showAbandonDialog) {
        AlertDialog(
            onDismissRequest = { showAbandonDialog = false },
            containerColor = colors.cardBackground,
            titleContentColor = colors.textPrimary,
            textContentColor = colors.textSecondary,
            title = {
                Text(
                    text = "Abandon Mission?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "This will permanently delete \"${journey.title}\" and all its progress. This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showAbandonDialog = false
                        onGiveUp(journey.id)
                    }
                ) {
                    Text("Abandon", color = colors.destructive, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAbandonDialog = false }) {
                    Text("Cancel", color = colors.textSecondary)
                }
            }
        )
    }
}

@Composable
private fun HeroCard(
    isComplete: Boolean,
    daysLeft: Int,
    percentage: Int,
    journey: Journey,
    estDate: String,
    animatedProgress: Float,
    scale: Float,
) {
    val gridLineColor = Color.White.copy(alpha = 0.03f)
    val colors = UntilDoneTheme.colors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(colors.focusCardStart, colors.focusCardEnd)
                )
            )
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val gridSize = 12.dp.toPx()
            val cols = (size.width / gridSize).toInt() + 1
            val rows = (size.height / gridSize).toInt() + 1
            for (i in 0..cols) {
                drawLine(
                    color = gridLineColor,
                    start = Offset(i * gridSize, 0f),
                    end = Offset(i * gridSize, size.height),
                    strokeWidth = 1f
                )
            }
            for (j in 0..rows) {
                drawLine(
                    color = gridLineColor,
                    start = Offset(0f, j * gridSize),
                    end = Offset(size.width, j * gridSize),
                    strokeWidth = 1f
                )
            }
        }

        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF10B981).copy(alpha = 0.12f),
                        Color(0xFF10B981).copy(alpha = 0.04f),
                        Color.Transparent
                    ),
                    center = Offset(size.width - 36.dp.toPx(), 28.dp.toPx()),
                    radius = 70.dp.toPx()
                ),
                center = Offset(size.width - 36.dp.toPx(), 28.dp.toPx()),
                radius = 70.dp.toPx()
            )
        }

        Column(modifier = Modifier.padding((20 * scale).dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                if (isComplete) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF10B981).copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Emerald400,
                                modifier = Modifier.size(10.dp)
                            )
                            Text(
                                text = "MISSION COMPLETE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp,
                                color = Emerald400
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF262626))
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            PulseIcon(
                                modifier = Modifier.size(12.dp),
                                color = Color(0xFFD4D4D4)
                            )
                            Text(
                                text = "PACE: ${journey.dailyTarget} ${journey.unit.uppercase()}/DAY",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp,
                                color = Color(0xFFD4D4D4)
                            )
                        }
                    }
                }

                Text(
                    text = "$percentage%",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isComplete) {
                Text(
                    text = "Done",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-2).sp,
                    color = Emerald400
                )
            } else {
                Text(
                    text = "$daysLeft",
                    fontSize = (56 * scale).sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-3).sp,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = (56 * scale).sp,
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.White, Color(0xFF737373))
                        )
                    )
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = (4 * scale).dp)
            ) {
                Text(
                    text = if (isComplete) "MISSION ACCOMPLISHED" else "DAYS TO GOAL",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = Color(0xFF9CA3AF)
                )
                if (!isComplete) {
                    Text(text = "  •  ", fontSize = 10.sp, color = Color(0xFF525252))
                    MinimalCalendarIcon(
                        modifier = Modifier.size(10.dp),
                        color = Color(0xFF737373)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "Est: $estDate",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp,
                        color = Color(0xFF737373)
                    )
                }
            }

            Spacer(modifier = Modifier.height((18 * scale).dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF262626).copy(alpha = 0.8f))
                    .padding(1.5.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0xFF0C0C0E))
            ) {
                val fillFraction = animatedProgress.coerceIn(0f, 1f)
                if (fillFraction > 0.01f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fillFraction.coerceAtLeast(0.03f))
                            .height(12.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(
                                Brush.horizontalGradient(listOf(Emerald600, Emerald400))
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .background(
                                    Color.White.copy(alpha = 0.2f),
                                    RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp)
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LogProgressCard(
    journey: Journey,
    todayCompleted: Int,
    logAmount: Int,
    onLogAmountChange: (Int) -> Unit,
    isMinReached: Boolean,
    isMaxReached: Boolean,
    remainingUnits: Int,
    scale: Float,
    onCommit: () -> Unit
) {
    val colors = UntilDoneTheme.colors
    val todayMet = todayCompleted >= journey.dailyTarget
    val todayBadgeColor = when {
        journey.dailyMax != null && todayCompleted >= journey.dailyMax -> Emerald400
        todayMet -> Color.White
        else -> Color(0xFFFBBF24)
    }
    val displayColor = when {
        isMaxReached -> Emerald400
        isMinReached -> Color.White
        else -> Color(0xFFFBBF24)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(colors.cardBackground)
    ) {
        Column(modifier = Modifier.padding((20 * scale).dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Log Progress",
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (journey.dailyMax != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Min: ${journey.dailyTarget}",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isMinReached) colors.textPrimary else colors.textTertiary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "  •  ",
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textTertiary
                            )
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = if (isMaxReached) Emerald400 else colors.textTertiary,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "Max: ${journey.dailyMax}",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isMaxReached) Emerald400 else colors.textTertiary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        Text(
                            text = "How many ${journey.unit} did you complete?",
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textTertiary
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(colors.tagBackground)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "TODAY: $todayCompleted / ${journey.dailyTarget}",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                        color = todayBadgeColor
                    )
                }
            }

            Spacer(modifier = Modifier.height((18 * scale).dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size((44 * scale).dp)
                        .clip(CircleShape)
                        .background(colors.stepperBackground)
                        .clickable(enabled = logAmount > 1) {
                            onLogAmountChange(logAmount - 1)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease",
                        tint = colors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$logAmount",
                        fontSize = (52 * scale).sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-2).sp,
                        color = displayColor
                    )
                    Text(
                        text = if (logAmount == 1) journey.unit.trimEnd('s').uppercase()
                        else journey.unit.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = displayColor.copy(alpha = 0.8f)
                    )
                }

                Box(
                    modifier = Modifier
                        .size((44 * scale).dp)
                        .clip(CircleShape)
                        .background(colors.stepperBackground)
                        .clickable(enabled = logAmount < remainingUnits) {
                            onLogAmountChange(logAmount + 1)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase",
                        tint = colors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (remainingUnits > 1) {
                Spacer(modifier = Modifier.height(12.dp))
                Slider(
                    value = logAmount.toFloat(),
                    onValueChange = { onLogAmountChange(it.toInt()) },
                    valueRange = 1f..remainingUnits.toFloat(),
                    steps = (remainingUnits - 2).coerceAtLeast(0),
                    colors = SliderDefaults.colors(
                        thumbColor = if (isMaxReached) Emerald400 else Color.White,
                        activeTrackColor = if (isMaxReached) Emerald500 else colors.textSecondary,
                        inactiveTrackColor = colors.background
                    )
                )
            }

            Spacer(modifier = Modifier.height((16 * scale).dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((50 * scale).dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.background)
                    .border(
                        width = 1.dp,
                        color = colors.textTertiary.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable(onClick = onCommit),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isMaxReached) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = Emerald400,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = colors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "Commit Progress",
                        style = MaterialTheme.typography.labelLarge,
                        color = colors.textPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun StatTile(
    label: String,
    value: String,
    suffix: String,
    scale: Float,
    modifier: Modifier = Modifier
) {
    val colors = UntilDoneTheme.colors
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colors.cardBackground)
            .padding((16 * scale).dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = colors.textTertiary
        )
        Spacer(modifier = Modifier.height((6 * scale).dp))
        Row {
            Text(
                text = value,
                fontSize = (22 * scale).sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                modifier = Modifier.alignByBaseline()
            )
            Text(
                text = suffix,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = colors.textTertiary,
                modifier = Modifier.alignByBaseline()
            )
        }
    }
}

@Composable
private fun FocusTimeCard(timeSpentSeconds: Int, scale: Float) {
    val colors = UntilDoneTheme.colors
    val h = timeSpentSeconds / 3600
    val m = (timeSpentSeconds % 3600) / 60
    val s = timeSpentSeconds % 60

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(colors.cardBackground)
            .padding((16 * scale).dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colors.tagBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccessTime,
                    contentDescription = null,
                    tint = colors.emerald,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Focus Time",
                    style = MaterialTheme.typography.titleSmall,
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "TOTAL DURATION",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = colors.textTertiary
                )
            }
        }

        Row(verticalAlignment = Alignment.Bottom) {
            if (h > 0) {
                Text(
                    "$h",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = colors.textPrimary
                )
                Text(
                    "h ",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textTertiary,
                    modifier = Modifier.padding(start = 1.dp, bottom = 4.dp)
                )
            }
            if (h > 0 || m > 0) {
                Text(
                    "$m",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = colors.textPrimary
                )
                Text(
                    "m ",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textTertiary,
                    modifier = Modifier.padding(start = 1.dp, bottom = 4.dp)
                )
            }
            Text(
                "$s",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = colors.textPrimary
            )
            Text(
                "s",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textTertiary,
                modifier = Modifier.padding(start = 1.dp, bottom = 4.dp)
            )
        }
    }
}

@Composable
private fun PulseIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val midY = h / 2f
        val path = Path().apply {
            moveTo(0f, midY)
            lineTo(w * 0.20f, midY)
            lineTo(w * 0.30f, h * 0.15f)
            lineTo(w * 0.42f, h * 0.85f)
            lineTo(w * 0.55f, h * 0.25f)
            lineTo(w * 0.65f, midY)
            lineTo(w, midY)
        }
        drawPath(path = path, color = color, style = Stroke(width = 1.8f, cap = StrokeCap.Round))
    }
}

@Composable
private fun MinimalCalendarIcon(modifier: Modifier = Modifier, color: Color) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val pad = w * 0.1f
        val cornerR = w * 0.12f

        drawRoundRect(
            color = color,
            topLeft = Offset(pad, pad + h * 0.15f),
            size = androidx.compose.ui.geometry.Size(w - pad * 2, h - pad * 2 - h * 0.1f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerR, cornerR),
            style = Stroke(width = 1.4f)
        )
        drawLine(
            color = color,
            start = Offset(pad, pad + h * 0.4f),
            end = Offset(w - pad, pad + h * 0.4f),
            strokeWidth = 1.2f
        )
        val hookW = 1.4f
        drawLine(
            color = color,
            start = Offset(w * 0.32f, pad),
            end = Offset(w * 0.32f, pad + h * 0.22f),
            strokeWidth = hookW,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(w * 0.68f, pad),
            end = Offset(w * 0.68f, pad + h * 0.22f),
            strokeWidth = hookW,
            cap = StrokeCap.Round
        )
    }
}
