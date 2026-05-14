package com.hermes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class NotificationSettings(
    val enableDeactivationReminder: Boolean = true,
    val enableWeeklyReport: Boolean = false,
    val reminderDaysBefore: Int = 7
)

data class SecuritySettings(
    val passwordSet: Boolean = false,
    val biometricEnabled: Boolean = false,
    val autoLockMinutes: Int = 5
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    // TODO: Inject DataStore or SharedPreferences for persistence
) : ViewModel() {

    private val _notificationSettings = MutableStateFlow(NotificationSettings())
    val notificationSettings: StateFlow<NotificationSettings> = _notificationSettings.asStateFlow()

    private val _securitySettings = MutableStateFlow(SecuritySettings())
    val securitySettings: StateFlow<SecuritySettings> = _securitySettings.asStateFlow()

    fun updateNotificationSetting(key: String, enabled: Boolean) {
        val current = _notificationSettings.value
        _notificationSettings.value = when (key) {
            "deactivation_reminder" -> NotificationSettings(
                enableDeactivationReminder = enabled,
                enableWeeklyReport = current.enableWeeklyReport,
                reminderDaysBefore = current.reminderDaysBefore
            )
            "weekly_report" -> NotificationSettings(
                enableDeactivationReminder = current.enableDeactivationReminder,
                enableWeeklyReport = enabled,
                reminderDaysBefore = current.reminderDaysBefore
            )
            else -> current
        }
    }

    fun updateSecuritySetting(key: String, enabled: Boolean) {
        val current = _securitySettings.value
        _securitySettings.value = when (key) {
            "biometric" -> SecuritySettings(
                passwordSet = current.passwordSet,
                biometricEnabled = enabled,
                autoLockMinutes = current.autoLockMinutes
            )
            else -> current
        }
    }

    fun setPassword(password: String) {
        // TODO: Persist password securely
        _securitySettings.value = SecuritySettings(
            passwordSet = true,
            biometricEnabled = _securitySettings.value.biometricEnabled,
            autoLockMinutes = _securitySettings.value.autoLockMinutes
        )
    }
}