package com.loanmaster.pro.feature.settings

import android.Manifest
import android.os.Build
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.loanmaster.pro.core.managers.NotificationHelper

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

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid


@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: SettingsViewModel,
    onClearHistory: () -> Unit = {},
    onNavigateBottomNav: (String) -> Unit = {},
    activeBottomNavItem: String = "settings"
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dummyCurrency = com.loanmaster.pro.LocalCurrency.current
    var showUnlockDialog by rememberSaveable { mutableStateOf(false) }
    val language = uiState.language
    val currency = uiState.currency
    val notificationsEnabled = uiState.notificationsEnabled
    val keepHistoryEnabled = uiState.keepHistoryEnabled
    val remindersEnabled = uiState.remindersEnabled
    val emiDueDay = uiState.emiDueDay
    val emiReminderHour = uiState.emiReminderTimeHour
    val emiReminderMinute = uiState.emiReminderTimeMinute
    val emiReminderDays = uiState.emiReminderDays

    Scaffold(
        topBar = { SettingsTopBar(onNavigateBack) },
        containerColor = BackgroundDark
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.sm),
            verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.components.iconSmall)
        ) {
            // Language selector removed for this release
            item { DefaultCurrencySection(currency, viewModel::setCurrency) }
            item { PreferencesSection(notificationsEnabled, keepHistoryEnabled, viewModel::setNotificationsEnabled, viewModel::setKeepHistoryEnabled) }
            item { RemindersSection(remindersEnabled, viewModel::setRemindersEnabled, emiDueDay, viewModel::setEmiDueDay, emiReminderHour, emiReminderMinute, viewModel::setEmiReminderTime, emiReminderDays, viewModel::setEmiReminderDays) }
            item { DataBackupSection(onClearHistory = onClearHistory) }
            item { SupportAppSection(onPremiumClick = { showUnlockDialog = true }) }
            item { AboutSupportSection() }
            item { AccountSyncSection(onPremiumClick = { showUnlockDialog = true }) }

            item { Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.md)) }
        }
        if (showUnlockDialog) {
            val context = androidx.compose.ui.platform.LocalContext.current
            com.loanmaster.pro.core.ui.PremiumUnlockDialog(
                onDismiss = { showUnlockDialog = false },
                onUnlockSuccessful = {
                    com.loanmaster.pro.core.managers.PremiumManager(context).unlockPermanent()
                }
            )
        }

    }
}

@Composable
private fun SettingsTopBar(onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(LoanMasterTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .size(LoanMasterTheme.spacing.xl)
                .clip(CircleShape)
                .background(SurfaceDark)
                .border(1.dp, CardStroke, CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = TextPrimary)
        }
        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.md))
        Column {
            Text("Settings", color = TextPrimary, fontSize = LoanMasterTheme.typography.title.fontSize, fontWeight = FontWeight.Bold)
            Text("Customize your app experience", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(LoanMasterTheme.spacing.md))
            .background(SurfaceDark)
            .border(1.dp, CardStroke, RoundedCornerShape(LoanMasterTheme.spacing.md))
            .padding(LoanMasterTheme.spacing.md)
    ) {
        Text(title, color = AccentYellow, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = LoanMasterTheme.spacing.md))
        content()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LanguageSection(selectedLang: String, onLanguageChange: (String) -> Unit) {
    val languages = listOf("English", "हिंदी", "ગુજરાતી")
    
    // FIX: Add disclaimer and disable buttons
    SectionCard(title = "Language") {
        Row(Modifier.fillMaxWidth().padding(bottom = LoanMasterTheme.spacing.md), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Select Language", color = TextPrimary, fontSize = LoanMasterTheme.typography.body.fontSize)
            Text(selectedLang, color = TextSecondary, fontSize = LoanMasterTheme.typography.body.fontSize)
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.sm),
            verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.sm)
        ) {
            languages.forEach { lang ->
                val isSelected = selectedLang == lang
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(LoanMasterTheme.components.iconSmall))
                        .background(if (isSelected) AccentBlue.copy(alpha = 0.2f) else BackgroundDark.copy(alpha = 0.4f))
                        .border(1.dp, if (isSelected) AccentBlue else CardStroke.copy(alpha = 0.4f), RoundedCornerShape(LoanMasterTheme.components.iconSmall))
                        .padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isSelected) {
                        Icon(Icons.Rounded.Check, contentDescription = null, tint = AccentBlue.copy(alpha = 0.4f), modifier = Modifier.size(LoanMasterTheme.spacing.md))
                        Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.xs))
                    }
                    Text(lang, color = if (isSelected) AccentBlue.copy(alpha = 0.4f) else TextSecondary.copy(alpha = 0.4f), fontSize = LoanMasterTheme.typography.body.fontSize)
                }
            }
        }
        Spacer(Modifier.heightIn(min = LoanMasterTheme.spacing.sm))
        Text(
            text = "Translation coming soon. The app currently displays in English only.",
            color = TextSecondary,
            fontSize = LoanMasterTheme.typography.body.fontSize,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DefaultCurrencySection(selectedCurrency: String, onCurrencyChange: (String) -> Unit) {
    val currencies = listOf("INR (₹)", "USD (\$)", "AED (د.إ)", "EUR (€)", "GBP (£)")
    
    SectionCard(title = "Default Currency") {
        Row(Modifier.fillMaxWidth().padding(bottom = LoanMasterTheme.spacing.md), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Select Currency", color = TextPrimary, fontSize = LoanMasterTheme.typography.body.fontSize)
            Text(selectedCurrency, color = TextSecondary, fontSize = LoanMasterTheme.typography.body.fontSize)
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.sm),
            verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.sm)
        ) {
            currencies.forEach { currency ->
                val isSelected = selectedCurrency == currency
                val highlightColor = Color(0xFF4CAF50) // Green highlight
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(LoanMasterTheme.components.iconSmall))
                        .background(if (isSelected) highlightColor.copy(alpha = 0.2f) else BackgroundDark)
                        .border(1.dp, if (isSelected) highlightColor else CardStroke, RoundedCornerShape(LoanMasterTheme.components.iconSmall))
                        .clickable { onCurrencyChange(currency) }
                        .padding(horizontal = LoanMasterTheme.spacing.md, vertical = LoanMasterTheme.spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(currency, color = if (isSelected) highlightColor else TextSecondary, fontSize = LoanMasterTheme.typography.body.fontSize)
                }
            }
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color = AccentBlue,
    titleColor: Color = TextPrimary,
    onClick: () -> Unit = {},
    trailingContent: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = LoanMasterTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(LoanMasterTheme.spacing.lg))
        Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.md))
        Column(Modifier.weight(1f)) {
            Text(title, color = titleColor, fontSize = LoanMasterTheme.typography.body.fontSize)
            if (subtitle.isNotEmpty()) {
                Text(subtitle, color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
            }
        }
        trailingContent()
    }
}

@Composable
private fun PreferencesSection(
    notificationsEnabled: Boolean,
    keepHistoryEnabled: Boolean,
    onNotificationsChange: (Boolean) -> Unit,
    onKeepHistoryChange: (Boolean) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onNotificationsChange(true)
            NotificationHelper.sendImmediateNotification(
                context, 
                "Notifications Enabled", 
                "You will now receive finance reminders."
            )
        } else {
            onNotificationsChange(false)
        }
    }

    SectionCard(title = "Preferences") {
        SettingsRow(
            icon = Icons.Rounded.Numbers,
            title = "Number Format",
            subtitle = "1,23,456.78",
            trailingContent = { Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = TextSecondary) }
        )
        SettingsRow(
            icon = Icons.Rounded.DateRange,
            title = "Date Format",
            subtitle = "20 May 2025",
            trailingContent = { Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = TextSecondary) }
        )
        SettingsRow(
            icon = Icons.Rounded.Notifications,
            title = "Notifications",
            subtitle = "Enable alerts",
            trailingContent = {
                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { checked -> 
                        if (checked) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                                    onNotificationsChange(true)
                                    NotificationHelper.sendImmediateNotification(context, "Notifications Enabled", "You will now receive finance reminders.")
                                } else {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            } else {
                                onNotificationsChange(true)
                                NotificationHelper.sendImmediateNotification(context, "Notifications Enabled", "You will now receive finance reminders.")
                            }
                        } else {
                            onNotificationsChange(false)
                        }
                    },
                    colors = SwitchDefaults.colors(checkedThumbColor = AccentBlue, checkedTrackColor = AccentBlue.copy(alpha=0.5f))
                )
            }
        )
        SettingsRow(
            icon = Icons.Rounded.History,
            title = "Keep History",
            subtitle = "Save calculations",
            trailingContent = {
                Switch(
                    checked = keepHistoryEnabled,
                    onCheckedChange = { onKeepHistoryChange(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = AccentBlue, checkedTrackColor = AccentBlue.copy(alpha=0.5f))
                )
            }
        )
}
}

fun getOrdinal(n: Int): String {
    if (n in 11..13) return "th"
    return when (n % 10) {
        1 -> "st"
        2 -> "nd"
        3 -> "rd"
        else -> "th"
    }
}

@Composable
private fun RemindersSection(
    remindersEnabled: Boolean,
    onRemindersChange: (Boolean) -> Unit,
    emiDueDay: Int,
    onEmiDueDayChange: (Int) -> Unit,
    reminderHour: Int,
    reminderMinute: Int,
    onReminderTimeChange: (Int, Int) -> Unit,
    emiReminderDays: Set<String>,
    onEmiReminderDaysChange: (Set<String>) -> Unit
) {
    var showDayPicker by remember { mutableStateOf(false) }
    var showFrequencyPicker by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current

    fun showTimePicker() {
        android.app.TimePickerDialog(
            context,
            { _, hourOfDay, minute -> 
                onReminderTimeChange(hourOfDay, minute)
            },
            reminderHour,
            reminderMinute,
            false // 12 hour format
        ).show()
    }

    SectionCard(title = "Reminders") {
        if (showDayPicker) {
            AlertDialog(
                onDismissRequest = { showDayPicker = false },
                title = { Text("Select EMI Due Day", color = TextPrimary) },
                text = {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(LoanMasterTheme.components.iconLarge),
                        horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.sm),
                        verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.sm),
                        modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp)
                    ) {
                        items(31) { index ->
                            val day = index + 1
                            val isSelected = day == emiDueDay
                            Box(
                                modifier = Modifier
                                    .size(LoanMasterTheme.components.iconLarge)
                                    .clip(CircleShape)
                                    .background(if (isSelected) AccentBlue else Color.Transparent)
                                    .clickable {
                                        onEmiDueDayChange(day)
                                        showDayPicker = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(day.toString(), color = if (isSelected) Color.White else TextPrimary)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showDayPicker = false }) {
                        Text("Close", color = AccentBlue)
                    }
                },
                containerColor = BackgroundDark
            )
        }

        if (showFrequencyPicker) {
            val frequencyOptions = listOf(
                "0" to "Same Day",
                "1" to "1 Day Before",
                "2" to "2 Days Before",
                "3" to "3 Days Before",
                "4" to "4 Days Before",
                "5" to "5 Days Before",
                "7" to "7 Days Before"
            )
            AlertDialog(
                onDismissRequest = { showFrequencyPicker = false },
                title = { Text("Reminder Frequency", color = TextPrimary) },
                text = {
                    LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp)) {
                        items(frequencyOptions.size) { index ->
                            val option = frequencyOptions[index]
                            val isSelected = emiReminderDays.contains(option.first)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val newDays = if (isSelected) {
                                            emiReminderDays - option.first
                                        } else {
                                            emiReminderDays + option.first
                                        }
                                        onEmiReminderDaysChange(newDays.ifEmpty { setOf("0") })
                                    }
                                    .padding(vertical = LoanMasterTheme.spacing.md),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = null,
                                    colors = CheckboxDefaults.colors(checkedColor = AccentBlue)
                                )
                                Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.md))
                                Text(option.second, color = TextPrimary)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showFrequencyPicker = false }) {
                        Text("Close", color = AccentBlue)
                    }
                },
                containerColor = BackgroundDark
            )
        }

        SettingsRow(
            icon = Icons.Rounded.Alarm,
            title = "EMI Reminders",
            subtitle = "Get notified before your next EMI is due",
            trailingContent = {
                Switch(
                    checked = remindersEnabled,
                    onCheckedChange = null,
                    enabled = false,
                    colors = SwitchDefaults.colors(checkedThumbColor = AccentBlue, checkedTrackColor = AccentBlue.copy(alpha=0.5f))
                )
            }
        )
        if (remindersEnabled) {
            SettingsRow(
                icon = Icons.Rounded.CalendarMonth,
                title = "EMI Due Date",
                subtitle = "${emiDueDay}${getOrdinal(emiDueDay)} of every month",
                onClick = { showDayPicker = true },
                trailingContent = { Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = TextSecondary) }
            )
            val frequencyText = emiReminderDays.map {
                if (it == "0") "Same Day" else "$it Days Before"
            }.joinToString(", ")

            SettingsRow(
                icon = Icons.Rounded.Event,
                title = "Reminder Frequency",
                subtitle = frequencyText,
                onClick = { showFrequencyPicker = true },
                trailingContent = { Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = TextSecondary) }
            )
            val isPM = reminderHour >= 12
            val displayHour = if (reminderHour % 12 == 0) 12 else reminderHour % 12
            val displayMinute = String.format(java.util.Locale.US, "%02d", reminderMinute)
            val timeString = "$displayHour:$displayMinute ${if(isPM) "PM" else "AM"}"

            SettingsRow(
                icon = Icons.Rounded.AccessTime,
                title = "Reminder Time",
                subtitle = timeString,
                onClick = { showTimePicker() },
                trailingContent = { Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = TextSecondary) }
            )
        }
    }
}

@Composable
private fun DataBackupSection(onClearHistory: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Clear All History", color = TextPrimary) },
            text = { Text("Are you sure you want to delete all calculation history? This cannot be undone.", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = { 
                    onClearHistory()
                    showDialog = false
                    android.widget.Toast.makeText(context, "History Cleared", android.widget.Toast.LENGTH_SHORT).show()
                }) {
                    Text("Clear", color = DestructiveRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel", color = AccentBlue)
                }
            },
            containerColor = BackgroundDark
        )
    }

    SectionCard(title = "Data & Backup") {
        SettingsRow(
            icon = Icons.Rounded.CloudUpload,
            title = "Backup & Restore",
            subtitle = "Backup your data to restore later",
            trailingContent = { Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = TextSecondary) }
        )
        SettingsRow(
            icon = Icons.Rounded.ImportExport,
            title = "Export All History",
            subtitle = "Export all calculations as CSV",
            trailingContent = { Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = TextSecondary) }
        )
        SettingsRow(
            icon = Icons.Rounded.Delete,
            title = "Clear All History",
            subtitle = "This action cannot be undone",
            iconTint = DestructiveRed,
            titleColor = DestructiveRed,
            onClick = { showDialog = true },
            trailingContent = { Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = DestructiveRed) }
        )
    }
}

@Composable
private fun AboutSupportSection() {
    SectionCard(title = "About & Support") {
        SettingsRow(
            icon = Icons.Rounded.Info,
            title = "About LoanMaster Pro",
            subtitle = "Version 1.0.0",
            trailingContent = { Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = TextSecondary) }
        )
        SettingsRow(
            icon = Icons.Rounded.Star,
            title = "Rate Us",
            subtitle = "If you like our app, please rate us",
            trailingContent = { Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = TextSecondary) }
        )
        SettingsRow(
            icon = Icons.Rounded.HeadsetMic,
            title = "Contact Support",
            subtitle = "We're here to help you",
            trailingContent = { Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = TextSecondary) }
        )
        SettingsRow(
            icon = Icons.Rounded.Security,
            title = "Privacy Policy",
            subtitle = "Read our privacy policy",
            trailingContent = { Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = TextSecondary) }
        )
    }
}

@Composable
private fun AccountSyncSection(onPremiumClick: () -> Unit = {}) {
    SectionCard(title = "Account & Sync") {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = LoanMasterTheme.spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.CloudSync, contentDescription = null, tint = AccentBlue, modifier = Modifier.size(LoanMasterTheme.spacing.xl))
            Spacer(Modifier.widthIn(min = LoanMasterTheme.spacing.md))
            Column(Modifier.weight(1f)) {
                Text("Sign in to Sync", color = TextPrimary, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold)
                Text("Sync your history across devices", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize)
            }
            OutlinedButton(
                onClick = { },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentBlue),
                border = androidx.compose.foundation.BorderStroke(1.dp, AccentBlue),
                shape = RoundedCornerShape(LoanMasterTheme.components.iconSmall)
            ) {
                Text("Sign In")
            }
        }
    }
        }



@Composable
private fun SupportAppSection(onPremiumClick: () -> Unit = {}) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var isAdPlaying by remember { mutableStateOf(false) }

    SectionCard(title = "Premium & Support") {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = LoanMasterTheme.spacing.sm),
            verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = LoanMasterTheme.spacing.md)) {
                    Text(
                        text = "Unlock Premium",
                        color = Color.White,
                        fontSize = LoanMasterTheme.typography.body.fontSize,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Remove ads and get advanced features.",
                        color = TextSecondary,
                        fontSize = LoanMasterTheme.typography.label.fontSize
                    )
                }
                Button(
                    onClick = onPremiumClick,
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen, contentColor = BackgroundDark)
                ) {
                    Text("Buy Premium")
                }
            }
            
            HorizontalDivider(color = SurfaceDark)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = LoanMasterTheme.spacing.md)) {
                    Text(
                        text = "Support with Ads",
                        color = Color.White,
                        fontSize = LoanMasterTheme.typography.body.fontSize,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Watch a short ad to support the developer.",
                        color = TextSecondary,
                        fontSize = LoanMasterTheme.typography.label.fontSize
                    )
                }
                Button(
                    onClick = {
                        val activity = context as? android.app.Activity
                        if (activity != null) {
                            isAdPlaying = true
                            com.loanmaster.pro.core.ads.RewardedAdManager.showAd(activity) {
                                isAdPlaying = false
                                android.widget.Toast.makeText(context, "Thank you for your support!", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    enabled = !isAdPlaying,
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark, contentColor = Color.White)
                ) {
                    Text("Watch Ad")
                }
            }
        }
    }
}

