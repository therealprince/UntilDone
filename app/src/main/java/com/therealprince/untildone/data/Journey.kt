package com.therealprince.untildone.data

data class Journey(
    val id: Long = 0,
    val userId: Long = 0,
    val title: String,
    val tag: String,
    val progress: Int,
    val target: Int,
    val dailyTarget: Int,
    val dailyMax: Int? = null,
    val unit: String,
    val timeSpent: Int = 0,
    val todayCompleted: Int = 0,
    val lastLogDate: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    val percentage: Int
        get() = if (target > 0) ((progress.toFloat() / target) * 100).toInt().coerceIn(0, 100) else 0

    val isComplete: Boolean
        get() = progress >= target

    val remaining: Int
        get() = (target - progress).coerceAtLeast(0)

    fun todayCompletedFor(today: String): Int =
        if (lastLogDate == today) todayCompleted else 0
}
