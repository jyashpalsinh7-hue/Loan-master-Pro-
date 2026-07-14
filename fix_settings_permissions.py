import os

file_path = "app/src/main/java/com/loanmaster/pro/feature/settings/SettingsScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

target = """@Composable
private fun PreferencesSection(
    notificationsEnabled: Boolean,
    keepHistoryEnabled: Boolean,
    onNotificationsChange: (Boolean) -> Unit,
    onKeepHistoryChange: (Boolean) -> Unit
) {"""

replacement = """import android.Manifest
import android.os.Build
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.loanmaster.pro.core.managers.NotificationHelper

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
"""

content = content.replace(target, replacement)

target2 = """                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { onNotificationsChange(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = AccentBlue, checkedTrackColor = AccentBlue.copy(alpha=0.5f))
                )"""

replacement2 = """                Switch(
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
                )"""

content = content.replace(target2, replacement2)

with open(file_path, "w") as f:
    f.write(content)

print("Done")
