package com.hermes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.data.preferences.UserPreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationSettings(
    val enableDeactivationReminder: Boolean = true,
    val enableWeeklyReport: Boolean = false,
    val enable30DayReminder: Boolean = true,
    val enable7DayReminder: Boolean = true,
    val enable3DayReminder: Boolean = true,
    val enable1DayReminder: Boolean = false,
    val reminderDaysBefore: Int = 7
)

data class SecuritySettings(
    val passwordSet: Boolean = false,
    val biometricEnabled: Boolean = false,
    val autoLockMinutes: Int = 5
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _notificationSettings = MutableStateFlow(NotificationSettings())
    val notificationSettings: StateFlow<NotificationSettings> = _notificationSettings.asStateFlow()

    private val _securitySettings = MutableStateFlow(SecuritySettings())
    val securitySettings: StateFlow<SecuritySettings> = _securitySettings.asStateFlow()

    init {
        loadSettings()
    }

    /**
     * Load settings from DataStore
     */
    private fun loadSettings() {
        viewModelScope.launch {
            // Load notification settings
            userPreferencesManager.enableDeactivationReminder.collect { enabled ->
                _notificationSettings.value = _notificationSettings.value.copy(
                    enableDeactivationReminder = enabled
                )
            }
        }
        viewModelScope.launch {
            userPreferencesManager.enableWeeklyReport.collect { enabled ->
                _notificationSettings.value = _notificationSettings.value.copy(
                    enableWeeklyReport = enabled
                )
            }
        }
        viewModelScope.launch {
            userPreferencesManager.enable30DayReminder.collect { enabled ->
                _notificationSettings.value = _notificationSettings.value.copy(
                    enable30DayReminder = enabled
                )
            }
        }
        viewModelScope.launch {
            userPreferencesManager.enable7DayReminder.collect { enabled ->
                _notificationSettings.value = _notificationSettings.value.copy(
                    enable7DayReminder = enabled
                )
            }
        }
        viewModelScope.launch {
            userPreferencesManager.enable3DayReminder.collect { enabled ->
                _notificationSettings.value = _notificationSettings.value.copy(
                    enable3DayReminder = enabled
                )
            }
        }
        viewModelScope.launch {
            userPreferencesManager.enable1DayReminder.collect { enabled ->
                _notificationSettings.value = _notificationSettings.value.copy(
                    enable1DayReminder = enabled
                )
            }
        }

        // Load security settings
        viewModelScope.launch {
            userPreferencesManager.passwordSet.collect { set ->
                _securitySettings.value = _securitySettings.value.copy(
                    passwordSet = set
                )
            }
        }
        viewModelScope.launch {
            userPreferencesManager.biometricEnabled.collect { enabled ->
                _securitySettings.value = _securitySettings.value.copy(
                    biometricEnabled = enabled
                )
            }
        }
        viewModelScope.launch {
            userPreferencesManager.autoLockMinutes.collect { minutes ->
                _securitySettings.value = _securitySettings.value.copy(
                    autoLockMinutes = minutes
                )
            }
        }
    }

    /**
     * Update notification setting and persist to DataStore
     */
    fun updateNotificationSetting(key: String, enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesManager.updateNotificationSetting(key, enabled)
        }
        // Update local state immediately for UI feedback
        val current = _notificationSettings.value
        _notificationSettings.value = when (key) {
            "deactivation_reminder" -> current.copy(enableDeactivationReminder = enabled)
            "weekly_report" -> current.copy(enableWeeklyReport = enabled)
            "30_day_reminder" -> current.copy(enable30DayReminder = enabled)
            "7_day_reminder" -> current.copy(enable7DayReminder = enabled)
            "3_day_reminder" -> current.copy(enable3DayReminder = enabled)
            "1_day_reminder" -> current.copy(enable1DayReminder = enabled)
            else -> current
        }
    }

    /**
     * Update security setting and persist to DataStore
     */
    fun updateSecuritySetting(key: String, enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesManager.updateSecuritySetting(key, enabled)
        }
        // Update local state immediately for UI feedback
        val current = _securitySettings.value
        _securitySettings.value = when (key) {
            "biometric" -> current.copy(biometricEnabled = enabled)
            else -> current
        }
    }

    /**
     * Set password and persist to DataStore
     */
    fun setPassword(password: String) {
        viewModelScope.launch {
            userPreferencesManager.setPassword(password)
        }
        _securitySettings.value = _securitySettings.value.copy(passwordSet = true)
    }

    /**
     * Clear password from DataStore
     */
    fun clearPassword() {
        viewModelScope.launch {
            userPreferencesManager.clearPassword()
        }
        _securitySettings.value = SecuritySettings()
    }
}