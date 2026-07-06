package com.loanmaster.pro.feature.settings

import com.loanmaster.pro.domain.model.*
import com.loanmaster.pro.feature.gst.*
import com.loanmaster.pro.feature.sip.*
import com.loanmaster.pro.core.ui.*
import com.loanmaster.pro.feature.history.*
import com.loanmaster.pro.core.theme.*
import com.loanmaster.pro.data.datastore.*
import com.loanmaster.pro.feature.rd.*
import com.loanmaster.pro.domain.calculator.*
import com.loanmaster.pro.data.local.entity.*
import com.loanmaster.pro.core.utils.*
import com.loanmaster.pro.data.local.dao.*
import com.loanmaster.pro.data.local.room.*
import com.loanmaster.pro.feature.emi.*
import com.loanmaster.pro.feature.loansummary.*
import com.loanmaster.pro.feature.prepayment.*
import com.loanmaster.pro.core.formatter.*
import com.loanmaster.pro.feature.fd.*
import com.loanmaster.pro.data.repository.*
import com.loanmaster.pro.feature.currency.*
import com.loanmaster.pro.core.navigation.*
import com.loanmaster.pro.feature.compare.*
import com.loanmaster.pro.feature.loaneligibility.*
import com.loanmaster.pro.core.responsive.*
import com.loanmaster.pro.feature.home.*

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch


class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SettingsRepository(application)
    
                    
    val uiState: StateFlow<SettingsUiState> = combine(
        repository.language,
        repository.currency,
        repository.notificationsEnabled,
        repository.keepHistoryEnabled,
        repository.remindersEnabled,
        repository.emiDueDay,
        repository.emiReminderTimeHour,
        repository.emiReminderTimeMinute,
        repository.emiReminderDays
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        SettingsUiState(
            language = values[0] as String,
            currency = values[1] as String,
            notificationsEnabled = values[2] as Boolean,
            keepHistoryEnabled = values[3] as Boolean,
            remindersEnabled = values[4] as Boolean,
            emiDueDay = values[5] as Int,
            emiReminderTimeHour = values[6] as Int,
            emiReminderTimeMinute = values[7] as Int,
            emiReminderDays = values[8] as Set<String>
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun setLanguage(lang: String) { viewModelScope.launch { repository.setLanguage(lang) } }
    fun setCurrency(curr: String) { viewModelScope.launch { repository.setCurrency(curr) } }
    fun setNotificationsEnabled(enabled: Boolean) { viewModelScope.launch { repository.setNotificationsEnabled(enabled) } }
    fun setKeepHistoryEnabled(enabled: Boolean) { viewModelScope.launch { repository.setKeepHistoryEnabled(enabled) } }
    fun setRemindersEnabled(enabled: Boolean) { viewModelScope.launch { repository.setRemindersEnabled(enabled) } }
    fun setEmiDueDay(day: Int) { viewModelScope.launch { repository.setEmiDueDay(day) } }
    fun setEmiReminderTime(hour: Int, minute: Int) { viewModelScope.launch { repository.setEmiReminderTime(hour, minute) } }
    fun setEmiReminderDays(days: Set<String>) { viewModelScope.launch { repository.setEmiReminderDays(days) } }
}
