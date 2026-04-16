package com.example.untildone.data

data class DailyLog(
    val id: Long = 0,
    val userId: Long,
    val date: String,  // "yyyy-MM-dd"
    val progressLogged: Int = 0,
    val focusMinutes: Int = 0,
    val journeysWorkedOn: Int = 0
)
