package com.example.untildone.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

class AppDatabase private constructor(context: Context) :
    SQLiteOpenHelper(context.applicationContext, DB_NAME, null, DB_VERSION) {

    // Flow for journey changes
    private val _journeyUpdates = MutableStateFlow(0L)

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                passwordHash TEXT NOT NULL,
                createdAt INTEGER NOT NULL
            )"""
        )
        db.execSQL(
            """CREATE TABLE journeys (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                userId INTEGER NOT NULL,
                title TEXT NOT NULL,
                tag TEXT NOT NULL,
                progress INTEGER NOT NULL,
                target INTEGER NOT NULL,
                dailyTarget INTEGER NOT NULL,
                unit TEXT NOT NULL,
                createdAt INTEGER NOT NULL
            )"""
        )
        db.execSQL(
            """CREATE TABLE focus_sessions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                userId INTEGER NOT NULL,
                durationSeconds INTEGER NOT NULL,
                completedAt INTEGER NOT NULL
            )"""
        )
        db.execSQL(
            """CREATE TABLE daily_logs (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                userId INTEGER NOT NULL,
                date TEXT NOT NULL,
                progressLogged INTEGER NOT NULL DEFAULT 0,
                focusMinutes INTEGER NOT NULL DEFAULT 0,
                journeysWorkedOn INTEGER NOT NULL DEFAULT 0,
                UNIQUE(userId, date)
            )"""
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // For v1, no migrations needed
    }

    // Notify that journeys changed
    fun notifyJourneyChange() {
        _journeyUpdates.value = System.currentTimeMillis()
    }

    fun journeyUpdatesFlow(): Flow<Long> = _journeyUpdates

    // ==================== USER OPERATIONS ====================

    suspend fun getUserByEmail(email: String): User? = withContext(Dispatchers.IO) {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users WHERE email = ? LIMIT 1", arrayOf(email))
        cursor.use {
            if (it.moveToFirst()) it.toUser() else null
        }
    }

    suspend fun insertUser(user: User): Long = withContext(Dispatchers.IO) {
        val db = writableDatabase
        db.insert("users", null, ContentValues().apply {
            put("name", user.name)
            put("email", user.email)
            put("passwordHash", user.passwordHash)
            put("createdAt", user.createdAt)
        })
    }

    suspend fun emailExists(email: String): Boolean = withContext(Dispatchers.IO) {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT 1 FROM users WHERE email = ?", arrayOf(email))
        cursor.use { it.moveToFirst() }
    }

    suspend fun getUserById(id: Long): User? = withContext(Dispatchers.IO) {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM users WHERE id = ?", arrayOf(id.toString()))
        cursor.use {
            if (it.moveToFirst()) it.toUser() else null
        }
    }

    // ==================== JOURNEY OPERATIONS ====================

    suspend fun getJourneysByUser(userId: Long): List<Journey> = withContext(Dispatchers.IO) {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM journeys WHERE userId = ? ORDER BY createdAt DESC",
            arrayOf(userId.toString())
        )
        cursor.use { it.toJourneyList() }
    }

    suspend fun getJourneyById(id: Long): Journey? = withContext(Dispatchers.IO) {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM journeys WHERE id = ?", arrayOf(id.toString()))
        cursor.use {
            if (it.moveToFirst()) it.toJourney() else null
        }
    }

    suspend fun insertJourney(journey: Journey): Long = withContext(Dispatchers.IO) {
        val db = writableDatabase
        val id = db.insert("journeys", null, ContentValues().apply {
            put("userId", journey.userId)
            put("title", journey.title)
            put("tag", journey.tag)
            put("progress", journey.progress)
            put("target", journey.target)
            put("dailyTarget", journey.dailyTarget)
            put("unit", journey.unit)
            put("createdAt", journey.createdAt)
        })
        notifyJourneyChange()
        id
    }

    suspend fun updateJourney(journey: Journey) = withContext(Dispatchers.IO) {
        val db = writableDatabase
        db.update("journeys", ContentValues().apply {
            put("progress", journey.progress)
            put("title", journey.title)
            put("tag", journey.tag)
            put("target", journey.target)
            put("dailyTarget", journey.dailyTarget)
            put("unit", journey.unit)
        }, "id = ?", arrayOf(journey.id.toString()))
        notifyJourneyChange()
    }

    suspend fun deleteJourneyById(id: Long) = withContext(Dispatchers.IO) {
        val db = writableDatabase
        db.delete("journeys", "id = ?", arrayOf(id.toString()))
        notifyJourneyChange()
    }

    suspend fun getAllJourneysByUser(userId: Long): List<Journey> = getJourneysByUser(userId)

    suspend fun deleteAllJourneysByUser(userId: Long) = withContext(Dispatchers.IO) {
        val db = writableDatabase
        db.delete("journeys", "userId = ?", arrayOf(userId.toString()))
        notifyJourneyChange()
    }

    // ==================== FOCUS SESSION OPERATIONS ====================

    suspend fun insertFocusSession(session: FocusSession): Long = withContext(Dispatchers.IO) {
        val db = writableDatabase
        db.insert("focus_sessions", null, ContentValues().apply {
            put("userId", session.userId)
            put("durationSeconds", session.durationSeconds)
            put("completedAt", session.completedAt)
        })
    }

    suspend fun getAllFocusSessionsByUser(userId: Long): List<FocusSession> =
        withContext(Dispatchers.IO) {
            val db = readableDatabase
            val cursor = db.rawQuery(
                "SELECT * FROM focus_sessions WHERE userId = ?",
                arrayOf(userId.toString())
            )
            cursor.use { it.toFocusSessionList() }
        }

    suspend fun deleteAllFocusSessionsByUser(userId: Long) = withContext(Dispatchers.IO) {
        val db = writableDatabase
        db.delete("focus_sessions", "userId = ?", arrayOf(userId.toString()))
    }

    // ==================== DAILY LOG OPERATIONS ====================

    suspend fun upsertDailyLog(log: DailyLog) = withContext(Dispatchers.IO) {
        val db = writableDatabase
        val existing = getDailyLogByDate(log.userId, log.date)
        if (existing != null) {
            db.update("daily_logs", ContentValues().apply {
                put("progressLogged", log.progressLogged)
                put("focusMinutes", log.focusMinutes)
                put("journeysWorkedOn", log.journeysWorkedOn)
            }, "id = ?", arrayOf(existing.id.toString()))
        } else {
            db.insert("daily_logs", null, ContentValues().apply {
                put("userId", log.userId)
                put("date", log.date)
                put("progressLogged", log.progressLogged)
                put("focusMinutes", log.focusMinutes)
                put("journeysWorkedOn", log.journeysWorkedOn)
            })
        }
    }

    suspend fun getDailyLogByDate(userId: Long, date: String): DailyLog? =
        withContext(Dispatchers.IO) {
            val db = readableDatabase
            val cursor = db.rawQuery(
                "SELECT * FROM daily_logs WHERE userId = ? AND date = ? LIMIT 1",
                arrayOf(userId.toString(), date)
            )
            cursor.use {
                if (it.moveToFirst()) it.toDailyLog() else null
            }
        }

    suspend fun getDailyLogsSince(userId: Long, startDate: String): List<DailyLog> =
        withContext(Dispatchers.IO) {
            val db = readableDatabase
            val cursor = db.rawQuery(
                "SELECT * FROM daily_logs WHERE userId = ? AND date >= ? ORDER BY date ASC",
                arrayOf(userId.toString(), startDate)
            )
            cursor.use { it.toDailyLogList() }
        }

    suspend fun getTotalPoints(userId: Long): Int = withContext(Dispatchers.IO) {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT COALESCE(SUM(progressLogged), 0) FROM daily_logs WHERE userId = ?",
            arrayOf(userId.toString())
        )
        cursor.use {
            if (it.moveToFirst()) it.getInt(0) else 0
        }
    }

    suspend fun getAllDailyLogsByUser(userId: Long): List<DailyLog> =
        withContext(Dispatchers.IO) {
            val db = readableDatabase
            val cursor = db.rawQuery(
                "SELECT * FROM daily_logs WHERE userId = ? ORDER BY date DESC",
                arrayOf(userId.toString())
            )
            cursor.use { it.toDailyLogList() }
        }

    suspend fun deleteAllDailyLogsByUser(userId: Long) = withContext(Dispatchers.IO) {
        val db = writableDatabase
        db.delete("daily_logs", "userId = ?", arrayOf(userId.toString()))
    }

    // ==================== ACCOUNT OPERATIONS ====================

    suspend fun resetUserData(userId: Long) = withContext(Dispatchers.IO) {
        val db = writableDatabase
        db.delete("journeys", "userId = ?", arrayOf(userId.toString()))
        db.delete("focus_sessions", "userId = ?", arrayOf(userId.toString()))
        db.delete("daily_logs", "userId = ?", arrayOf(userId.toString()))
        notifyJourneyChange()
    }

    suspend fun deleteUser(userId: Long) = withContext(Dispatchers.IO) {
        val db = writableDatabase
        db.delete("journeys", "userId = ?", arrayOf(userId.toString()))
        db.delete("focus_sessions", "userId = ?", arrayOf(userId.toString()))
        db.delete("daily_logs", "userId = ?", arrayOf(userId.toString()))
        db.delete("users", "id = ?", arrayOf(userId.toString()))
        notifyJourneyChange()
    }

    // ==================== CURSOR EXTENSIONS ====================

    private fun Cursor.toUser(): User = User(
        id = getLong(getColumnIndexOrThrow("id")),
        name = getString(getColumnIndexOrThrow("name")),
        email = getString(getColumnIndexOrThrow("email")),
        passwordHash = getString(getColumnIndexOrThrow("passwordHash")),
        createdAt = getLong(getColumnIndexOrThrow("createdAt"))
    )

    private fun Cursor.toJourney(): Journey = Journey(
        id = getLong(getColumnIndexOrThrow("id")),
        userId = getLong(getColumnIndexOrThrow("userId")),
        title = getString(getColumnIndexOrThrow("title")),
        tag = getString(getColumnIndexOrThrow("tag")),
        progress = getInt(getColumnIndexOrThrow("progress")),
        target = getInt(getColumnIndexOrThrow("target")),
        dailyTarget = getInt(getColumnIndexOrThrow("dailyTarget")),
        unit = getString(getColumnIndexOrThrow("unit")),
        createdAt = getLong(getColumnIndexOrThrow("createdAt"))
    )

    private fun Cursor.toJourneyList(): List<Journey> {
        val list = mutableListOf<Journey>()
        while (moveToNext()) list.add(toJourney())
        return list
    }

    private fun Cursor.toFocusSession(): FocusSession = FocusSession(
        id = getLong(getColumnIndexOrThrow("id")),
        userId = getLong(getColumnIndexOrThrow("userId")),
        durationSeconds = getInt(getColumnIndexOrThrow("durationSeconds")),
        completedAt = getLong(getColumnIndexOrThrow("completedAt"))
    )

    private fun Cursor.toFocusSessionList(): List<FocusSession> {
        val list = mutableListOf<FocusSession>()
        while (moveToNext()) list.add(toFocusSession())
        return list
    }

    private fun Cursor.toDailyLog(): DailyLog = DailyLog(
        id = getLong(getColumnIndexOrThrow("id")),
        userId = getLong(getColumnIndexOrThrow("userId")),
        date = getString(getColumnIndexOrThrow("date")),
        progressLogged = getInt(getColumnIndexOrThrow("progressLogged")),
        focusMinutes = getInt(getColumnIndexOrThrow("focusMinutes")),
        journeysWorkedOn = getInt(getColumnIndexOrThrow("journeysWorkedOn"))
    )

    private fun Cursor.toDailyLogList(): List<DailyLog> {
        val list = mutableListOf<DailyLog>()
        while (moveToNext()) list.add(toDailyLog())
        return list
    }

    companion object {
        private const val DB_NAME = "untildone_database"
        private const val DB_VERSION = 1

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = AppDatabase(context)
                INSTANCE = instance
                instance
            }
        }
    }
}
