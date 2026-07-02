package com.example

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid

private val BgColor = Color(0xFF020B2D)
private val CardColor = Color(0xFF071D4D)
private val BorderColor = Color(0xFF123D9A)
private val PrimaryBlue = Color(0xFF2D7FF9)
private val GoldAccent = Color(0xFFF5B82E)
private val TextPrimary = Color.White
private val TextSecondary = Color(0xFFB8C5E0)
private val DestructiveRed = Color(0xFFE53935)
private val NavBackground = Color(0xFF0A1128)

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: SettingsViewModel,
    onClearHistory: () -> Unit = {},
    onNavigateBottomNav: (String) -> Unit = {},
    activeBottomNavItem: String = "settings"
) {
    val language by viewModel.language.collectAsStateWithLifecycle()
    val currency by viewModel.currency.collectAsStateWithLifecycle()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsStateWithLifecycle()
    val keepHistoryEnabled by viewModel.keepHistoryEnabled.collectAsStateWithLifecycle()
    val remindersEnabled by viewModel.remindersEnabled.collectAsStateWithLifecycle()
    val emiDueDay by viewModel.emiDueDay.collectAsStateWithLifecycle()
    val emiReminderHour by viewModel.emiReminderTimeHour.collectAsStateWithLifecycle()
    val emiReminderMinute by viewModel.emiReminderTimeMinute.collectAsStateWithLifecycle()
    val emiReminderDays by viewModel.emiReminderDays.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { SettingsTopBar(onNavigateBack) },
        containerColor = BgColor
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Language selector removed for this release
            item { DefaultCurrencySection(currency, viewModel::setCurrency) }
            item { PreferencesSection(notificationsEnabled, keepHistoryEnabled, viewModel::setNotificationsEnabled, viewModel::setKeepHistoryEnabled) }
            item { RemindersSection(remindersEnabled, viewModel::setRemindersEnabled, emiDueDay, viewModel::setEmiDueDay, emiReminderHour, emiReminderMinute, viewModel::setEmiReminderTime, emiReminderDays, viewModel::setEmiReminderDays) }
            item { DataBackupSection(onClearHistory = onClearHistory) }
            item { AboutSupportSection() }
            item { AccountSyncSection() }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun SettingsTopBar(onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(CardColor)
                .border(1.dp, BorderColor, CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = TextPrimary)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text("Settings", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Customize your app experience", color = TextSecondary, fontSize = 12.sp)
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardColor)
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(title, color = GoldAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
        content()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LanguageSection(selectedLang: String, onLanguageChange: (String) -> Unit) {
    val languages = listOf("English", "हिंदी", "ગુજરાતી")
    
    SectionCard(title = "Language") {
        Row(Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Select Language", color = TextPrimary, fontSize = 16.sp)
            Text(selectedLang, color = TextSecondary, fontSize = 14.sp)
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            languages.forEach { lang ->
                val isSelected = selectedLang == lang
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) PrimaryBlue.copy(alpha = 0.2f) else BgColor)
                        .border(1.dp, if (isSelected) PrimaryBlue else BorderColor, RoundedCornerShape(20.dp))
                        .clickable { onLanguageChange(lang) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isSelected) {
                        Icon(Icons.Rounded.Check, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                    }
                    Text(lang, color = if (isSelected) PrimaryBlue else TextSecondary, fontSize = 14.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DefaultCurrencySection(selectedCurrency: String, onCurrencyChange: (String) -> Unit) {
    val currencies = listOf("INR (₹)", "USD (\$)", "AED (د.إ)", "EUR (€)", "GBP (£)")
    
    SectionCard(title = "Default Currency") {
        Row(Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Select Currency", color = TextPrimary, fontSize = 16.sp)
            Text(selectedCurrency, color = TextSecondary, fontSize = 14.sp)
        }
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            currencies.forEach { currency ->
                val isSelected = selectedCurrency == currency
                val highlightColor = Color(0xFF4CAF50) // Green highlight
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) highlightColor.copy(alpha = 0.2f) else BgColor)
                        .border(1.dp, if (isSelected) highlightColor else BorderColor, RoundedCornerShape(20.dp))
                        .clickable { onCurrencyChange(currency) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(currency, color = if (isSelected) highlightColor else TextSecondary, fontSize = 14.sp)
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
    iconTint: Color = PrimaryBlue,
    titleColor: Color = TextPrimary,
    onClick: () -> Unit = {},
    trailingContent: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = titleColor, fontSize = 16.sp)
            if (subtitle.isNotEmpty()) {
                Text(subtitle, color = TextSecondary, fontSize = 12.sp)
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
                    onCheckedChange = { onNotificationsChange(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = PrimaryBlue, checkedTrackColor = PrimaryBlue.copy(alpha=0.5f))
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
                    colors = SwitchDefaults.colors(checkedThumbColor = PrimaryBlue, checkedTrackColor = PrimaryBlue.copy(alpha=0.5f))
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
                        columns = GridCells.Adaptive(48.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp)
                    ) {
                        items(31) { index ->
                            val day = index + 1
                            val isSelected = day == emiDueDay
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) PrimaryBlue else Color.Transparent)
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
                        Text("Close", color = PrimaryBlue)
                    }
                },
                containerColor = BgColor
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
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = null,
                                    colors = CheckboxDefaults.colors(checkedColor = PrimaryBlue)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(option.second, color = TextPrimary)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showFrequencyPicker = false }) {
                        Text("Close", color = PrimaryBlue)
                    }
                },
                containerColor = BgColor
            )
        }

        SettingsRow(
            icon = Icons.Rounded.Alarm,
            title = "EMI Reminders",
            subtitle = "Get notified before your next EMI is due",
            trailingContent = {
                Switch(
                    checked = remindersEnabled,
                    onCheckedChange = { onRemindersChange(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = PrimaryBlue, checkedTrackColor = PrimaryBlue.copy(alpha=0.5f))
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
                    Text("Cancel", color = PrimaryBlue)
                }
            },
            containerColor = BgColor
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
private fun AccountSyncSection() {
    SectionCard(title = "Account & Sync") {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.CloudSync, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text("Sign in to Sync", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("Sync your history across devices", color = TextSecondary, fontSize = 12.sp)
            }
            OutlinedButton(
                onClick = { },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryBlue),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Sign In")
            }
        }
    }
}

