package com.hermes.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "hermes_user_preferences")

/**
 * User preferences manager for storing UI state flags and settings
 */
@Singleton
class UserPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Keys
    private val GESTURE_HINT_SHOWN = booleanPreferencesKey("gesture_hint_shown_identifier_list")

    // Notification settings keys
    private val ENABLE_DEACTIVATION_REMINDER = booleanPreferencesKey("enable_deactivation_reminder")
    private val ENABLE_WEEKLY_REPORT = booleanPreferencesKey("enable_weekly_report")
    private val ENABLE_30_DAY_REMINDER = booleanPreferencesKey("enable_30_day_reminder")
    private val ENABLE_7_DAY_REMINDER = booleanPreferencesKey("enable_7_day_reminder")
    private val ENABLE_3_DAY_REMINDER = booleanPreferencesKey("enable_3_day_reminder")
    private val ENABLE_1_DAY_REMINDER = booleanPreferencesKey("enable_1_day_reminder")
    private val REMINDER_DAYS_BEFORE = intPreferencesKey("reminder_days_before")

    // Security settings keys
    private val PASSWORD_SET = booleanPreferencesKey("password_set")
    private val PASSWORD_PROTECTION_ENABLED = booleanPreferencesKey("password_protection_enabled")
    private val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
    private val AUTO_LOCK_MINUTES = intPreferencesKey("auto_lock_minutes")
    private val PASSWORD_HASH = stringPreferencesKey("password_hash")

    /**
     * Flow indicating if the gesture hint has been shown for the identifier list
     */
    val gestureHintShown: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[GESTURE_HINT_SHOWN] ?: false
    }

    /**
     * Mark the gesture hint as shown for the identifier list
     */
    suspend fun setGestureHintShown(shown: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[GESTURE_HINT_SHOWN] = shown
        }
    }

    // =====================
    // Notification Settings
    // =====================

    /**
     * Flow for deactivation reminder enabled status
     */
    val enableDeactivationReminder: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ENABLE_DEACTIVATION_REMINDER] ?: true
    }

    /**
     * Flow for weekly report enabled status
     */
    val enableWeeklyReport: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ENABLE_WEEKLY_REPORT] ?: false
    }

    /**
     * Flow for 30-day reminder enabled status
     */
    val enable30DayReminder: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ENABLE_30_DAY_REMINDER] ?: true
    }

    /**
     * Flow for 7-day reminder enabled status
     */
    val enable7DayReminder: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ENABLE_7_DAY_REMINDER] ?: true
    }

    /**
     * Flow for 3-day reminder enabled status
     */
    val enable3DayReminder: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ENABLE_3_DAY_REMINDER] ?: true
    }

    /**
     * Flow for 1-day reminder enabled status
     */
    val enable1DayReminder: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ENABLE_1_DAY_REMINDER] ?: false
    }

    /**
     * Update notification setting
     */
    suspend fun updateNotificationSetting(key: String, enabled: Boolean) {
        context.dataStore.edit { preferences ->
            when (key) {
                "deactivation_reminder" -> preferences[ENABLE_DEACTIVATION_REMINDER] = enabled
                "weekly_report" -> preferences[ENABLE_WEEKLY_REPORT] = enabled
                "30_day_reminder" -> preferences[ENABLE_30_DAY_REMINDER] = enabled
                "7_day_reminder" -> preferences[ENABLE_7_DAY_REMINDER] = enabled
                "3_day_reminder" -> preferences[ENABLE_3_DAY_REMINDER] = enabled
                "1_day_reminder" -> preferences[ENABLE_1_DAY_REMINDER] = enabled
            }
        }
    }

    // =====================
    // Security Settings
    // =====================

    /**
     * Flow for password set status
     */
    val passwordSet: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PASSWORD_SET] ?: false
    }

    /**
     * Flow for password protection enabled status (database encryption)
     */
    val passwordProtectionEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PASSWORD_PROTECTION_ENABLED] ?: false
    }

    /**
     * Set password protection enabled status
     */
    suspend fun setPasswordProtectionEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PASSWORD_PROTECTION_ENABLED] = enabled
        }
    }

    /**
     * Flow for biometric enabled status
     */
    val biometricEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[BIOMETRIC_ENABLED] ?: false
    }

    /**
     * Flow for auto lock minutes
     */
    val autoLockMinutes: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[AUTO_LOCK_MINUTES] ?: 5
    }

    /**
     * Flow for password hash (for verification)
     */
    val passwordHash: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PASSWORD_HASH]
    }

    /**
     * Update security setting
     */
    suspend fun updateSecuritySetting(key: String, enabled: Boolean) {
        context.dataStore.edit { preferences ->
            when (key) {
                "biometric" -> preferences[BIOMETRIC_ENABLED] = enabled
            }
        }
    }

    /**
     * Set password (stores hash for verification)
     * Note: In production, this should use proper encryption
     */
    suspend fun setPassword(password: String) {
        context.dataStore.edit { preferences ->
            preferences[PASSWORD_SET] = true
            // Simple hash for MVP - in production use proper crypto
            preferences[PASSWORD_HASH] = hashPassword(password)
        }
    }

    /**
     * Clear password
     */
    suspend fun clearPassword() {
        context.dataStore.edit { preferences ->
            preferences[PASSWORD_SET] = false
            preferences.remove(PASSWORD_HASH)
            preferences[BIOMETRIC_ENABLED] = false
        }
    }

    /**
     * Verify password
     */
    fun verifyPassword(storedHash: String?, inputPassword: String): Boolean {
        if (storedHash == null) return false
        return storedHash == hashPassword(inputPassword)
    }

    /**
     * Simple password hash (for MVP only)
     * In production, use proper cryptographic hashing
     */
    private fun hashPassword(password: String): String {
        // Simple implementation for MVP - use proper crypto in production
        val bytes = password.toByteArray()
        var hash = 0
        for (byte in bytes) {
            hash = 31 * hash + byte.toInt()
        }
        return hash.toString()
    }
}