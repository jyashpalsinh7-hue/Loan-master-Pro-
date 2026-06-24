package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SettingsRepository(application)

    val language: StateFlow<String> = repository.language.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "English"
    )

    val currency: StateFlow<String> = repository.currency.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "INR (₹)"
    )

    val notificationsEnabled: StateFlow<Boolean> = repository.notificationsEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    val keepHistoryEnabled: StateFlow<Boolean> = repository.keepHistoryEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    val remindersEnabled: StateFlow<Boolean> = repository.remindersEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val emiDueDay: StateFlow<Int> = repository.emiDueDay.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 5
    )

    val emiReminderTimeHour: StateFlow<Int> = repository.emiReminderTimeHour.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 10
    )

    val emiReminderTimeMinute: StateFlow<Int> = repository.emiReminderTimeMinute.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val emiReminderDays: StateFlow<Set<String>> = repository.emiReminderDays.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = setOf("3")
    )

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            repository.setLanguage(lang)
        }
    }

    fun setCurrency(curr: String) {
        viewModelScope.launch {
            repository.setCurrency(curr)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setNotificationsEnabled(enabled)
        }
    }

    fun setKeepHistoryEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setKeepHistoryEnabled(enabled)
        }
    }

    fun setRemindersEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.setRemindersEnabled(enabled)
        }
    }

    fun setEmiDueDay(day: Int) {
        viewModelScope.launch {
            repository.setEmiDueDay(day)
        }
    }

    fun setEmiReminderTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            repository.setEmiReminderTime(hour, minute)
        }
    }

    fun setEmiReminderDays(days: Set<String>) {
        viewModelScope.launch {
            repository.setEmiReminderDays(days)
        }
    }
}
