package com.example

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    companion object {
        val LANGUAGE = stringPreferencesKey("language")
        val CURRENCY = stringPreferencesKey("currency")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val KEEP_HISTORY_ENABLED = booleanPreferencesKey("keep_history_enabled")
        val REMINDERS_ENABLED = booleanPreferencesKey("reminders_enabled")
        val EMI_DUE_DAY = intPreferencesKey("emi_due_day")
        val EMI_REMINDER_TIME_HOUR = intPreferencesKey("emi_reminder_time_hour")
        val EMI_REMINDER_TIME_MINUTE = intPreferencesKey("emi_reminder_time_minute")
        val EMI_REMINDER_DAYS = stringSetPreferencesKey("emi_reminder_days")
    }

    val language: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE] ?: "English"
    }

    val currency: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[CURRENCY] ?: "INR (₹)"
    }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[NOTIFICATIONS_ENABLED] ?: true
    }

    val keepHistoryEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEEP_HISTORY_ENABLED] ?: true
    }

    val remindersEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[REMINDERS_ENABLED] ?: false
    }

    val emiDueDay: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[EMI_DUE_DAY] ?: 5
    }

    val emiReminderTimeHour: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[EMI_REMINDER_TIME_HOUR] ?: 10
    }

    val emiReminderTimeMinute: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[EMI_REMINDER_TIME_MINUTE] ?: 0
    }

    val emiReminderDays: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[EMI_REMINDER_DAYS] ?: setOf("3") // Default to 3 days before
    }

    suspend fun setLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE] = language
        }
    }

    suspend fun setCurrency(currency: String) {
        context.dataStore.edit { preferences ->
            preferences[CURRENCY] = currency
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setKeepHistoryEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEEP_HISTORY_ENABLED] = enabled
        }
    }

    suspend fun setRemindersEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[REMINDERS_ENABLED] = enabled
        }
    }

    suspend fun setEmiDueDay(day: Int) {
        context.dataStore.edit { preferences ->
            preferences[EMI_DUE_DAY] = day
        }
    }

    suspend fun setEmiReminderTime(hour: Int, minute: Int) {
        context.dataStore.edit { preferences ->
            preferences[EMI_REMINDER_TIME_HOUR] = hour
            preferences[EMI_REMINDER_TIME_MINUTE] = minute
        }
    }

    suspend fun setEmiReminderDays(days: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[EMI_REMINDER_DAYS] = days
        }
    }
}
