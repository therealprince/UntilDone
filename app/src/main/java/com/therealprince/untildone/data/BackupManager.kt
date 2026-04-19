package com.therealprince.untildone.data

import android.content.Context
import android.os.Environment
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BackupData(
    val version: Int = 2,
    val createdAt: String = "",
    val user: User? = null,
    val journeys: List<Journey> = emptyList(),
    val focusSessions: List<FocusSession> = emptyList(),
    val dailyLogs: List<DailyLog> = emptyList()
)

class BackupManager(private val context: Context) {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    companion object {
        private const val MAX_AUTO_BACKUPS = 3
    }

    /**
     * Returns the public backup root: Internal Storage > UntilDone > {username} > backup
     * Survives app uninstalls. Separated per user account.
     */
    private fun getPublicBackupRoot(userName: String): File {
        val safeName = userName.trim().replace(Regex("[^a-zA-Z0-9._-]"), "_")
        val root = File(
            Environment.getExternalStorageDirectory(),
            "UntilDone${File.separator}$safeName${File.separator}backup"
        )
        if (!root.exists()) root.mkdirs()
        return root
    }

    /**
     * Manual backups go to: UntilDone/backup/manual/
     */
    private fun getManualBackupDir(userName: String): File {
        val dir = File(getPublicBackupRoot(userName), "manual")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    /**
     * Automatic backups go to: UntilDone/backup/auto/
     * Only the latest [MAX_AUTO_BACKUPS] are kept.
     */
    private fun getAutoBackupDir(userName: String): File {
        val dir = File(getPublicBackupRoot(userName), "auto")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    /**
     * Creates a manual backup. All manual backups are kept forever.
     */
    suspend fun createBackup(db: AppDatabase, userId: Long, userName: String): File {
        return createBackupIn(db, userId, getManualBackupDir(userName), "manual")
    }

    /**
     * Creates an automatic backup. Only the latest [MAX_AUTO_BACKUPS] are retained.
     */
    suspend fun createAutoBackup(db: AppDatabase, userId: Long, userName: String): File {
        val file = createBackupIn(db, userId, getAutoBackupDir(userName), "auto")
        pruneAutoBackups(userName)
        return file
    }

    private suspend fun createBackupIn(
        db: AppDatabase,
        userId: Long,
        dir: File,
        prefix: String
    ): File {
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
        val file = File(dir, "untildone_${prefix}_$timestamp.json")
        file.writeText(json)
        return file
    }

    /**
     * Keeps only the latest [MAX_AUTO_BACKUPS] automatic backups, deletes older ones.
     */
    private fun pruneAutoBackups(userName: String) {
        val autoFiles = getAutoBackupDir(userName).listFiles()
            ?.filter { it.extension == "json" }
            ?.sortedByDescending { it.lastModified() }
            ?: return

        if (autoFiles.size > MAX_AUTO_BACKUPS) {
            autoFiles.drop(MAX_AUTO_BACKUPS).forEach { it.delete() }
        }
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

    /**
     * Restore from raw JSON string (used when reading via ContentResolver from SAF picker).
     */
    suspend fun restoreFromJson(
        db: AppDatabase,
        json: String,
        sessionManager: SessionManager
    ): Boolean {
        return try {
            val backupData = gson.fromJson(json, BackupData::class.java)

            val user = backupData.user ?: return false

            val existingUser = db.getUserByEmail(user.email)
            val userId = if (existingUser != null) {
                existingUser.id
            } else {
                db.insertUser(user.copy(id = 0))
            }

            db.deleteAllJourneysByUser(userId)
            db.deleteAllFocusSessionsByUser(userId)
            db.deleteAllDailyLogsByUser(userId)

            backupData.journeys.forEach { journey ->
                db.insertJourney(journey.copy(id = 0, userId = userId))
            }
            backupData.focusSessions.forEach { session ->
                db.insertFocusSession(session.copy(id = 0, userId = userId))
            }
            backupData.dailyLogs.forEach { log ->
                db.upsertDailyLog(log.copy(id = 0, userId = userId))
            }

            sessionManager.saveSession(userId, user.name, user.email)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Returns all backup files (manual + auto) sorted by newest first.
     */
    fun getBackupFiles(userName: String): List<File> {
        val manual = getManualBackupDir(userName).listFiles()
            ?.filter { it.extension == "json" } ?: emptyList()
        val auto = getAutoBackupDir(userName).listFiles()
            ?.filter { it.extension == "json" } ?: emptyList()
        return (manual + auto).sortedByDescending { it.lastModified() }
    }

    fun getLatestBackup(userName: String): File? {
        return getBackupFiles(userName).firstOrNull()
    }

    /**
     * Returns a user-friendly path string for displaying where backups are stored.
     */
    fun getBackupLocationDisplay(userName: String): String {
        val safeName = userName.trim().replace(Regex("[^a-zA-Z0-9._-]"), "_")
        return "Internal Storage > UntilDone > $safeName > backup"
    }
}
