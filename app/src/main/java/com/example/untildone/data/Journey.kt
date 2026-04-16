package com.example.untildone.data

data class Journey(
    val id: Long = 0,
    val userId: Long = 0,
    val title: String,
    val tag: String,
    val progress: Int,
    val target: Int,
    val dailyTarget: Int,
    val unit: String,
    val createdAt: Long = System.currentTimeMillis()
) {
    val percentage: Int
        get() = if (target > 0) ((progress.toFloat() / target) * 100).toInt().coerceIn(0, 100) else 0

    val isComplete: Boolean
        get() = progress >= target

    val remaining: Int
        get() = (target - progress).coerceAtLeast(0)
}
