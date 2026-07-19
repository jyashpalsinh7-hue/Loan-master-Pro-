import re

with open("app/src/main/java/com/loanmaster/pro/feature/home/HomeScreen.kt", "r") as f:
    content = f.read()

content = content.replace(
    'val searchQuery = uiState.searchQuery',
    'var showUnlockDialog by rememberSaveable { mutableStateOf(false) }\n    val searchQuery = uiState.searchQuery'
)

content = content.replace(
    'android.widget.Toast.makeText(context, "Premium Features Coming Soon!", android.widget.Toast.LENGTH_SHORT).show()',
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

with open("app/src/main/java/com/loanmaster/pro/feature/home/HomeScreen.kt", "w") as f:
    f.write(content)
