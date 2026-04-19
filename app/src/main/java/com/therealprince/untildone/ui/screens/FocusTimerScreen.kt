package com.therealprince.untildone.ui.screens

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.therealprince.untildone.data.Journey
import com.therealprince.untildone.ui.theme.UntilDoneTheme
import kotlinx.coroutines.delay

private enum class TimerMode { Focus, ShortBreak }

private val FocusColor = Color(0xFF10B981)
private val BreakColor = Color(0xFF06B6D4)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusTimerScreen(
    onBack: () -> Unit,
    onSessionTick: (journeyId: Long?, seconds: Int) -> Unit,
    onSessionComplete: (durationSeconds: Int) -> Unit,
    journeys: List<Journey>,
    todayFocusSeconds: Int,
    todaySessionsCount: Int,
    streakDays: Int,
    modifier: Modifier = Modifier
) {
    val colors = UntilDoneTheme.colors

    var mode by remember { mutableStateOf(TimerMode.Focus) }
    val presets = remember { listOf(15, 25, 50, 90) }
    var durationPreset by remember { mutableIntStateOf(25) }
    var timeLeft by remember { mutableIntStateOf(25 * 60) }
    var isActive by remember { mutableStateOf(false) }
    var isCustomizing by remember { mutableStateOf(false) }
    var customMinutes by remember { mutableIntStateOf(25) }
    var selectedJourneyId by remember { mutableStateOf<Long?>(journeys.firstOrNull()?.id) }
    var sheetOpen by remember { mutableStateOf(false) }

    val totalTime = durationPreset * 60
    val isCustomPreset = durationPreset !in presets

    fun applyPreset(mins: Int) {
        durationPreset = mins
        timeLeft = mins * 60
        isActive = false
    }

    fun changeMode(newMode: TimerMode) {
        mode = newMode
        val mins = if (newMode == TimerMode.Focus) 25 else 5
        applyPreset(mins)
        customMinutes = mins
        isCustomizing = false
    }

    LaunchedEffect(isActive, timeLeft, mode, selectedJourneyId) {
        if (isActive && timeLeft > 0) {
            delay(1000)
            timeLeft -= 1
            if (mode == TimerMode.Focus) {
                onSessionTick(selectedJourneyId, 1)
            }
        } else if (timeLeft == 0 && isActive) {
            isActive = false
            if (mode == TimerMode.Focus) {
                onSessionComplete(totalTime)
            }
        }
    }

    val themeColor = if (mode == TimerMode.Focus) FocusColor else BreakColor
    val glowColor by animateColorAsState(
        targetValue = if (isActive) themeColor.copy(alpha = 0.18f) else Color(0x1A262626),
        animationSpec = tween(800),
        label = "glow"
    )

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val progress = if (totalTime > 0) (totalTime - timeLeft).toFloat() / totalTime else 0f

    val selectedJourney = journeys.firstOrNull { it.id == selectedJourneyId }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val gridSize = 24.dp.toPx()
            val cols = (size.width / gridSize).toInt() + 1
            val rows = (size.height / gridSize).toInt() + 1
            val color = Color.White.copy(alpha = 0.03f)
            for (i in 0..cols) {
                drawLine(
                    color = color,
                    start = Offset(i * gridSize, 0f),
                    end = Offset(i * gridSize, size.height),
                    strokeWidth = 1f
                )
            }
            for (j in 0..rows) {
                drawLine(
                    color = color,
                    start = Offset(0f, j * gridSize),
                    end = Offset(size.width, j * gridSize),
                    strokeWidth = 1f
                )
            }
        }

        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.TopCenter)
                .padding(top = 120.dp)
                .clip(CircleShape)
                .background(glowColor)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleIconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Back",
                        tint = Color(0xFFA3A3A3),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "CURRENT PHASE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = Color(0xFF737373)
                    )
                    Text(
                        text = if (mode == TimerMode.Focus) "Deep Focus" else "Short Rest",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = themeColor
                    )
                }
                Spacer(modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF121214))
                    .border(1.dp, Color(0xFF262626), RoundedCornerShape(50))
                    .clickable { sheetOpen = true }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Bolt,
                    contentDescription = null,
                    tint = Color(0xFF737373),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = selectedJourney?.title ?: "General Focus",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD4D4D4)
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFF525252),
                    modifier = Modifier.size(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Box(
                modifier = Modifier.size(280.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val stroke = 6.dp.toPx()
                    val pad = stroke / 2
                    val arc = Size(size.width - stroke, size.height - stroke)
                    drawArc(
                        color = Color(0xFF18181B),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = Offset(pad, pad),
                        size = arc,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = if (mode == TimerMode.Focus)
                                listOf(Color(0xFF10B981), Color(0xFF047857), Color(0xFF10B981))
                            else
                                listOf(Color(0xFF06B6D4), Color(0xFF0369A1), Color(0xFF06B6D4))
                        ),
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        topLeft = Offset(pad, pad),
                        size = arc,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}",
                        fontSize = if (isActive) 64.sp else 52.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (isActive) Color.White else Color(0xFFD4D4D4),
                        letterSpacing = (-2).sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isActive) "ENGAGED" else "STANDBY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp,
                        color = if (isActive) themeColor else Color(0xFF525252)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleIconButton(
                    size = 48.dp,
                    onClick = {
                        isActive = false
                        timeLeft = totalTime
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset",
                        tint = Color(0xFFA3A3A3),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(if (isActive) Color(0xFF18181B) else Color.White)
                        .border(
                            width = 4.dp,
                            color = Color.Black,
                            shape = CircleShape
                        )
                        .clickable { isActive = !isActive },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isActive) "Pause" else "Play",
                        tint = if (isActive) Color.White else Color.Black,
                        modifier = Modifier.size(28.dp)
                    )
                }

                CircleIconButton(
                    size = 48.dp,
                    onClick = { timeLeft = 0 }
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Skip",
                        tint = Color(0xFFA3A3A3),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF121214))
                    .border(1.dp, Color(0xFF262626), RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ModeChip(
                    label = "FOCUS",
                    icon = { Icon(Icons.Default.Psychology, null, tint = if (mode == TimerMode.Focus) FocusColor else Color(0xFF737373), modifier = Modifier.size(12.dp)) },
                    selected = mode == TimerMode.Focus,
                    onClick = { changeMode(TimerMode.Focus) }
                )
                ModeChip(
                    label = "BREAK",
                    icon = { Icon(Icons.Default.Coffee, null, tint = if (mode == TimerMode.ShortBreak) BreakColor else Color(0xFF737373), modifier = Modifier.size(12.dp)) },
                    selected = mode == TimerMode.ShortBreak,
                    onClick = { changeMode(TimerMode.ShortBreak) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!isActive) {
                if (!isCustomizing) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        presets.forEach { mins ->
                            PresetChip(
                                label = "${mins}M",
                                selected = durationPreset == mins,
                                onClick = { applyPreset(mins) }
                            )
                        }
                        PresetChip(
                            label = if (isCustomPreset) "${durationPreset}M" else "·",
                            icon = if (!isCustomPreset) Icons.Default.Tune else null,
                            selected = isCustomPreset,
                            onClick = {
                                customMinutes = durationPreset
                                isCustomizing = true
                            }
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF121214))
                            .border(1.dp, Color(0xFF262626), RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("1", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF737373))
                        Slider(
                            value = customMinutes.toFloat(),
                            onValueChange = {
                                customMinutes = it.toInt().coerceIn(1, 120)
                                applyPreset(customMinutes)
                            },
                            valueRange = 1f..120f,
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(
                                thumbColor = Color.White,
                                activeTrackColor = themeColor,
                                inactiveTrackColor = Color.Black
                            )
                        )
                        Text(
                            text = "${customMinutes}M",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .clickable { isCustomizing = false },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Today's Performance",
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    val focusH = todayFocusSeconds / 3600
                    val focusM = (todayFocusSeconds % 3600) / 60
                    val focusedLabel = if (focusH > 0) "${focusH}h ${focusM}m" else "${focusM}m"
                    PerformanceTile(
                        icon = Icons.Outlined.AccessTime,
                        iconTint = FocusColor,
                        value = focusedLabel,
                        label = "FOCUSED",
                        modifier = Modifier.weight(1f)
                    )
                    PerformanceTile(
                        icon = Icons.Default.CheckCircle,
                        iconTint = BreakColor,
                        value = "$todaySessionsCount",
                        label = "SESSIONS",
                        modifier = Modifier.weight(1f)
                    )
                    PerformanceTile(
                        icon = Icons.Default.LocalFireDepartment,
                        iconTint = Color(0xFFF97316),
                        value = "$streakDays",
                        label = "STREAK",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        if (sheetOpen) {
            ModalBottomSheet(
                onDismissRequest = { sheetOpen = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = Color(0xFF121214),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 32.dp)
                ) {
                    Text(
                        text = "Select Focus Target",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    JourneySheetItem(
                        title = "General Focus",
                        tag = null,
                        selected = selectedJourneyId == null,
                        onClick = {
                            selectedJourneyId = null
                            sheetOpen = false
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    journeys.forEach { j ->
                        JourneySheetItem(
                            title = j.title,
                            tag = j.tag,
                            selected = selectedJourneyId == j.id,
                            onClick = {
                                selectedJourneyId = j.id
                                sheetOpen = false
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CircleIconButton(
    size: androidx.compose.ui.unit.Dp = 40.dp,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFF121214))
            .border(1.dp, Color(0xFF262626), CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) { content() }
}

@Composable
private fun ModeChip(
    label: String,
    icon: @Composable () -> Unit,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) Color(0xFF1A1A1D) else Color.Transparent)
            .border(
                width = if (selected) 1.dp else 0.dp,
                color = if (selected) Color(0xFF404040) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        icon()
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            color = if (selected) Color.White else Color(0xFF737373)
        )
    }
}

@Composable
private fun PresetChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) Color.White else Color.Transparent)
            .border(
                width = 1.dp,
                color = if (selected) Color.White else Color(0xFF262626),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            Icon(icon, null, tint = if (selected) Color.Black else Color(0xFF737373), modifier = Modifier.size(14.dp))
        } else {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = if (selected) Color.Black else Color(0xFF737373)
            )
        }
    }
}

@Composable
private fun PerformanceTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF121214))
            .border(1.dp, Color(0xFF262626), RoundedCornerShape(20.dp))
            .padding(vertical = 16.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, null, tint = iconTint, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            color = Color(0xFF737373)
        )
    }
}

@Composable
private fun JourneySheetItem(
    title: String,
    tag: String?,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) Color.White.copy(alpha = 0.10f) else Color(0xFF18181B))
            .border(
                width = 1.dp,
                color = if (selected) Color.White.copy(alpha = 0.20f) else Color(0xFF262626),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            if (tag != null) {
                Text(
                    text = tag,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = Color(0xFF737373)
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        if (selected) {
            Icon(Icons.Default.CheckCircle, null, tint = Color.White, modifier = Modifier.size(18.dp))
        }
    }
}
