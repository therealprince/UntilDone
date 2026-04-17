package com.therealprince.untildone.data

data class FocusSession(
    val id: Long = 0,
    val userId: Long,
    val durationSeconds: Int,
    val completedAt: Long = System.currentTimeMillis()
)
