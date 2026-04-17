package com.therealprince.untildone.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.therealprince.untildone.data.AppDatabase
import com.therealprince.untildone.data.DailyLog
import com.therealprince.untildone.ui.theme.Emerald400
import com.therealprince.untildone.ui.theme.UntilDoneTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private data class WeekDayData(val day: String, val done: Boolean)

@Composable
fun AnalyticsScreen(
    userId: Long,
    db: AppDatabase,
    modifier: Modifier = Modifier
) {
    val colors = UntilDoneTheme.colors

    // Live data
    var totalPoints by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var weekData by remember { mutableStateOf(listOf<WeekDayData>()) }
    var chartData by remember { mutableStateOf(listOf<Float>()) }

    LaunchedEffect(userId) {
        totalPoints = db.getTotalPoints(userId)
        val allLogs = db.getAllDailyLogsByUser(userId)
        streak = calculateStreak(allLogs)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        val weekStartDate = dateFormat.format(cal.time)
        val weekLogs = db.getDailyLogsSince(userId, weekStartDate)

        weekData = computeWeekData(weekLogs)
        chartData = computeChartData(weekLogs)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .padding(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        Text(
            text = "Insights",
            style = MaterialTheme.typography.headlineLarge,
            color = colors.textPrimary,
            modifier = Modifier.padding(top = 4.dp)
        )

        // Execution Points Hero Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(colors.invertedBackground)
                .padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.GpsFixed,
                contentDescription = null,
                tint = colors.invertedContent.copy(alpha = 0.1f),
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.TopEnd)
            )

            Column {
                Text(
                    text = "EXECUTION POINTS",
                    style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.5.sp),
                    color = colors.invertedSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "%,d".format(totalPoints),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black
                    ),
                    color = colors.invertedContent
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(colors.invertedContent.copy(alpha = 0.1f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocalFireDepartment,
                        contentDescription = null,
                        tint = Emerald400,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = if (streak > 0) "$streak Day Streak!" else "Start your streak!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.invertedContent
                    )
                }
            }
        }

        // Weekly Consistency
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(colors.cardBackground)
                .padding(20.dp)
        ) {
            Text(
                text = "This Week",
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val displayWeek = if (weekData.isNotEmpty()) weekData else defaultWeekData()
                displayWeek.forEach { day ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    if (day.done) colors.streakActive
                                    else colors.streakInactive
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (day.done) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Done",
                                    tint = colors.streakActiveContent,
                                    modifier = Modifier.size(14.dp)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(
                                            colors.streakInactiveContent.copy(alpha = 0.5f)
                                        )
                                )
                            }
                        }
                        Text(
                            text = day.day,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textTertiary
                        )
                    }
                }
            }
        }

        // Consistency Load Chart
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(colors.cardBackground)
                .padding(20.dp)
        ) {
            Text(
                text = "Consistency Load",
                style = MaterialTheme.typography.titleMedium,
                color = colors.textPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val displayChart = if (chartData.isNotEmpty()) chartData
            else listOf(0f, 0f, 0f, 0f, 0f, 0f, 0f)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                displayChart.forEach { height ->
                    var targetHeight by remember { mutableFloatStateOf(0f) }
                    val animatedHeight by animateFloatAsState(
                        targetValue = targetHeight,
                        animationSpec = tween(700),
                        label = "bar"
                    )
                    LaunchedEffect(height) {
                        targetHeight = height / 100f
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .height(112.dp)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(colors.chartBarBackground),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height((112 * animatedHeight).dp)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(colors.chartBarFill)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "MON",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textTertiary
                )
                Text(
                    text = "SUN",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textTertiary
                )
            }
        }
    }
}

private fun defaultWeekData(): List<WeekDayData> {
    return listOf(
        WeekDayData("M", false), WeekDayData("T", false),
        WeekDayData("W", false), WeekDayData("T", false),
        WeekDayData("F", false), WeekDayData("S", false),
        WeekDayData("S", false)
    )
}

private fun calculateStreak(logs: List<DailyLog>): Int {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val logDates = logs.map { it.date }.toSet()

    var streak = 0
    val cal = Calendar.getInstance()
    val todayStr = dateFormat.format(cal.time)

    if (!logDates.contains(todayStr)) {
        cal.add(Calendar.DAY_OF_YEAR, -1)
    }

    while (true) {
        val dateStr = dateFormat.format(cal.time)
        if (logDates.contains(dateStr)) {
            streak++
            cal.add(Calendar.DAY_OF_YEAR, -1)
        } else {
            break
        }
    }

    return streak
}

private fun computeWeekData(weekLogs: List<DailyLog>): List<WeekDayData> {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val dayNames = listOf("M", "T", "W", "T", "F", "S", "S")
    val logDates = weekLogs.map { it.date }.toSet()

    val result = mutableListOf<WeekDayData>()
    val cal = Calendar.getInstance()
    while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
        cal.add(Calendar.DAY_OF_YEAR, -1)
    }
    for (i in 0 until 7) {
        val dateStr = dateFormat.format(cal.time)
        result.add(WeekDayData(dayNames[i], logDates.contains(dateStr)))
        cal.add(Calendar.DAY_OF_YEAR, 1)
    }
    return result
}

private fun computeChartData(weekLogs: List<DailyLog>): List<Float> {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val logMap = weekLogs.associate { it.date to it.progressLogged }

    val result = mutableListOf<Float>()
    val cal = Calendar.getInstance()
    while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
        cal.add(Calendar.DAY_OF_YEAR, -1)
    }
    for (i in 0 until 7) {
        val dateStr = dateFormat.format(cal.time)
        result.add((logMap[dateStr] ?: 0).toFloat())
        cal.add(Calendar.DAY_OF_YEAR, 1)
    }

    val max = result.maxOrNull() ?: 1f
    return if (max > 0f) result.map { (it / max) * 100f }
    else result.map { 0f }
}
