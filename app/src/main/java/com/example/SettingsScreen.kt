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
import com.example.ui.theme.*

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
fun SettingsScreen(onNavigateBack: () -> Unit = {}) {
    Scaffold(
        topBar = { SettingsTopBar(onNavigateBack) },
        bottomBar = { SettingsBottomBar() },
        containerColor = BgColor
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { AppearanceSection() }
            item { LanguageSection() }
            item { DefaultCurrencySection() }
            item { PreferencesSection() }
            item { DataBackupSection() }
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

@Composable
private fun AppearanceSection() {
    var isDark by remember { mutableStateOf(true) }
    SectionCard(title = "Appearance") {
        Row(Modifier.fillMaxWidth().clickable { isDark = true }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.DarkMode, contentDescription = null, tint = if (isDark) PrimaryBlue else TextSecondary, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text("Dark Mode", color = TextPrimary, fontSize = 16.sp)
                Text("Use dark theme", color = TextSecondary, fontSize = 12.sp)
            }
            RadioButton(
                selected = isDark,
                onClick = { isDark = true },
                colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue, unselectedColor = TextSecondary)
            )
        }
        Row(Modifier.fillMaxWidth().clickable { isDark = false }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.LightMode, contentDescription = null, tint = if (!isDark) PrimaryBlue else TextSecondary, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text("Light Mode", color = TextPrimary, fontSize = 16.sp)
                Text("Use light theme", color = TextSecondary, fontSize = 12.sp)
            }
            RadioButton(
                selected = !isDark,
                onClick = { isDark = false },
                colors = RadioButtonDefaults.colors(selectedColor = PrimaryBlue, unselectedColor = TextSecondary)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun LanguageSection() {
    var selectedLang by remember { mutableStateOf("English") }
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
                        .clickable { selectedLang = lang }
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
private fun DefaultCurrencySection() {
    var selectedCurrency by remember { mutableStateOf("INR (₹)") }
    val currencies = listOf("INR (₹)", "USD ($)", "AED (د.إ)", "EUR (€)", "GBP (£)")
    
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
                        .clickable { selectedCurrency = currency }
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
private fun PreferencesSection() {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var keepHistoryEnabled by remember { mutableStateOf(true) }
    
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
                    onCheckedChange = { notificationsEnabled = it },
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
                    onCheckedChange = { keepHistoryEnabled = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = PrimaryBlue, checkedTrackColor = PrimaryBlue.copy(alpha=0.5f))
                )
            }
        )
    }
}

@Composable
private fun DataBackupSection() {
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

@Composable
private fun SettingsBottomBar() {
    NavigationBar(
        containerColor = NavBackground,
        contentColor = TextSecondary,
        tonalElevation = 8.dp
    ) {
        val selectedRoute = "settings"
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Rounded.Home, contentDescription = "Home") },
            label = { Text("Home", maxLines = 1, fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GoldAccent,
                selectedTextColor = GoldAccent,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Rounded.History, contentDescription = "History") },
            label = { Text("History", maxLines = 1, fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GoldAccent,
                selectedTextColor = GoldAccent,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Rounded.Calculate, contentDescription = "Calculate") },
            label = { Text("Calculate", maxLines = 1, fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GoldAccent,
                selectedTextColor = GoldAccent,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Rounded.CompareArrows, contentDescription = "Compare") },
            label = { Text("Compare", maxLines = 1, fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GoldAccent,
                selectedTextColor = GoldAccent,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = { Icon(Icons.Rounded.Settings, contentDescription = "Settings") },
            label = { Text("Settings", maxLines = 1, fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GoldAccent,
                selectedTextColor = GoldAccent,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = Color.Transparent
            )
        )
    }
}
