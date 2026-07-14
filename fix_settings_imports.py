import os

file_path = "app/src/main/java/com/loanmaster/pro/feature/settings/SettingsScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

# Remove the incorrectly placed imports
target_imports = """import android.Manifest
import android.os.Build
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.loanmaster.pro.core.managers.NotificationHelper

@Composable
private fun PreferencesSection"""

replacement_imports = """@Composable
private fun PreferencesSection"""

content = content.replace(target_imports, replacement_imports)

# Add them to the top
if "import android.Manifest" not in content:
    content = content.replace("package com.loanmaster.pro.feature.settings", "package com.loanmaster.pro.feature.settings\n\nimport android.Manifest\nimport android.os.Build\nimport android.content.pm.PackageManager\nimport androidx.activity.compose.rememberLauncherForActivityResult\nimport androidx.activity.result.contract.ActivityResultContracts\nimport androidx.core.content.ContextCompat\nimport com.loanmaster.pro.core.managers.NotificationHelper")

with open(file_path, "w") as f:
    f.write(content)

print("Done")
