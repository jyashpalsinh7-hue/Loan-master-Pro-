import re

with open("app/src/main/java/com/loanmaster/pro/feature/home/HomeScreen.kt", "r") as f:
    content = f.read()

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
    Scaffold("""

content = content.replace('    Scaffold(', dialog_code)

with open("app/src/main/java/com/loanmaster/pro/feature/home/HomeScreen.kt", "w") as f:
    f.write(content)
