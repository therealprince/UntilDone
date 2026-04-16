package com.example.untildone.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BackupData(
    val version: Int = 1,
    val createdAt: String = "",
    val user: User? = null,
    val journeys: List<Journey> = emptyList(),
    val focusSessions: List<FocusSession> = emptyList(),
    val dailyLogs: List<DailyLog> = emptyList()
)

class BackupManager(private val context: Context) {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    private fun getBackupDir(): File {
        val dir = File(context.getExternalFilesDir(null), "backups")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    suspend fun createBackup(db: AppDatabase, userId: Long): File {
        val user = db.getUserById(userId)
        val journeys = db.getAllJourneysByUser(userId)
        val focusSessions = db.getAllFocusSessionsByUser(userId)
        val dailyLogs = db.getAllDailyLogsByUser(userId)

        val backupData = BackupData(
            createdAt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(Date()),
            user = user,
            journeys = journeys,
            focusSessions = focusSessions,
            dailyLogs = dailyLogs
        )

        val json = gson.toJson(backupData)
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val file = File(getBackupDir(), "untildone_backup_$timestamp.json")
        file.writeText(json)
        return file
    }

    suspend fun restoreBackup(
        db: AppDatabase,
        file: File,
        sessionManager: SessionManager
    ): Boolean {
        return try {
            val json = file.readText()
            val backupData = gson.fromJson(json, BackupData::class.java)

            val user = backupData.user ?: return false

            // Check if user already exists
            val existingUser = db.getUserByEmail(user.email)
            val userId = if (existingUser != null) {
                existingUser.id
            } else {
                db.insertUser(user.copy(id = 0))
            }

            // Clear existing data for this user
            db.deleteAllJourneysByUser(userId)
            db.deleteAllFocusSessionsByUser(userId)
            db.deleteAllDailyLogsByUser(userId)

            // Restore journeys
            backupData.journeys.forEach { journey ->
                db.insertJourney(journey.copy(id = 0, userId = userId))
            }

            // Restore focus sessions
            backupData.focusSessions.forEach { session ->
                db.insertFocusSession(session.copy(id = 0, userId = userId))
            }

            // Restore daily logs
            backupData.dailyLogs.forEach { log ->
                db.upsertDailyLog(log.copy(id = 0, userId = userId))
            }

            // Update session
            sessionManager.saveSession(userId, user.name, user.email)

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getBackupFiles(): List<File> {
        return getBackupDir().listFiles()
            ?.filter { it.extension == "json" }
            ?.sortedByDescending { it.lastModified() }
            ?: emptyList()
    }

    fun getLatestBackup(): File? {
        return getBackupFiles().firstOrNull()
    }
}
