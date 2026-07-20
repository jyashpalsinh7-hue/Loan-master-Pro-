import re

with open("app/src/main/java/com/loanmaster/pro/feature/settings/SettingsScreen.kt", "r") as f:
    content = f.read()

content = content.replace(
    'val language by viewModel.language.collectAsStateWithLifecycle()',
    'var showUnlockDialog by rememberSaveable { mutableStateOf(false) }\n    val language by viewModel.language.collectAsStateWithLifecycle()'
)

content = content.replace(
    'android.widget.Toast.makeText(context, "Premium coming soon!", android.widget.Toast.LENGTH_SHORT).show()',
    'showUnlockDialog = true'
)

# Add the dialog at the end of the Scaffold
dialog_code = """
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
"""

content = re.sub(r'    \}\n\}$', dialog_code, content)

with open("app/src/main/java/com/loanmaster/pro/feature/settings/SettingsScreen.kt", "w") as f:
    f.write(content)
