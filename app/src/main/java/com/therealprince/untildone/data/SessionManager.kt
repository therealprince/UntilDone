package com.therealprince.untildone.data

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("untildone_session", Context.MODE_PRIVATE)

    fun isLoggedIn(): Boolean = prefs.getLong("user_id", -1L) != -1L

    fun getUserId(): Long = prefs.getLong("user_id", -1L)

    fun getUserName(): String = prefs.getString("user_name", "") ?: ""

    fun getUserEmail(): String = prefs.getString("user_email", "") ?: ""

    fun saveSession(userId: Long, name: String, email: String) {
        prefs.edit()
            .putLong("user_id", userId)
            .putString("user_name", name)
            .putString("user_email", email)
            .apply()
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    // Returns null if user has never set a preference (follow system)
    fun isDarkModePreference(): Boolean? {
        return if (prefs.contains("dark_mode")) prefs.getBoolean("dark_mode", false) else null
    }

    fun setDarkMode(isDark: Boolean) {
        prefs.edit().putBoolean("dark_mode", isDark).apply()
    }

    fun isPeriodicBackupEnabled(): Boolean = prefs.getBoolean("periodic_backup", false)

    fun setPeriodicBackupEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("periodic_backup", enabled).apply()
    }

    fun getBackupIntervalHours(): Int = prefs.getInt("backup_interval_hours", 24)

    fun setBackupIntervalHours(hours: Int) {
        prefs.edit().putInt("backup_interval_hours", hours).apply()
    }

    // Profile image
    fun getProfileImagePath(): String = prefs.getString("profile_image_path", "") ?: ""

    fun setProfileImagePath(path: String) {
        prefs.edit().putString("profile_image_path", path).apply()
    }

    // Categories
    fun getCategories(): List<String> {
        val custom = prefs.getStringSet("custom_categories", emptySet()) ?: emptySet()
        return DEFAULT_CATEGORIES + custom.sorted()
    }

    fun addCategory(category: String) {
        val current = prefs.getStringSet("custom_categories", emptySet())
            ?.toMutableSet() ?: mutableSetOf()
        current.add(category)
        prefs.edit().putStringSet("custom_categories", current).apply()
    }

    fun removeCategory(category: String) {
        if (category in DEFAULT_CATEGORIES) return
        val current = prefs.getStringSet("custom_categories", emptySet())
            ?.toMutableSet() ?: mutableSetOf()
        current.remove(category)
        prefs.edit().putStringSet("custom_categories", current).apply()
    }

    fun isDefaultCategory(category: String): Boolean = category in DEFAULT_CATEGORIES

    // Units
    fun getUnits(): List<String> {
        val custom = prefs.getStringSet("custom_units", emptySet()) ?: emptySet()
        return DEFAULT_UNITS + custom.sorted()
    }

    fun addUnit(unit: String) {
        val current = prefs.getStringSet("custom_units", emptySet())
            ?.toMutableSet() ?: mutableSetOf()
        current.add(unit)
        prefs.edit().putStringSet("custom_units", current).apply()
    }

    // Update flow
    fun getSkippedUpdateVersion(): String =
        prefs.getString("update_skipped_version", "") ?: ""

    fun setSkippedUpdateVersion(version: String) {
        prefs.edit().putString("update_skipped_version", version).apply()
    }

    fun getLastUpdateCheckMillis(): Long = prefs.getLong("update_last_check", 0L)

    fun setLastUpdateCheckMillis(time: Long) {
        prefs.edit().putLong("update_last_check", time).apply()
    }

    companion object {
        val DEFAULT_CATEGORIES = listOf("SKILL", "FITNESS", "READING", "MINDFUL", "WORK")
        val DEFAULT_UNITS = listOf("days", "sessions", "hours", "pages", "lectures")
    }
}
