package com.loanmaster.pro.feature.settings
import com.loanmaster.pro.data.local.entity.*
import com.loanmaster.pro.domain.model.*

data class SettingsUiState(
    val language: String = "English",
    val currency: String = "INR (₹)",
    val notificationsEnabled: Boolean = true,
    val keepHistoryEnabled: Boolean = true,
    val remindersEnabled: Boolean = false,
    val emiDueDay: Int = 5,
    val emiReminderTimeHour: Int = 10,
    val emiReminderTimeMinute: Int = 0,
    val emiReminderDays: Set<String> = setOf("3")
)
