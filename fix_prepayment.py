import re

with open("app/src/main/java/com/loanmaster/pro/feature/prepayment/PrepaymentScreen.kt", "r") as f:
    content = f.read()

# Replace the custom AlertDialog with PremiumUnlockDialog
# The custom dialog starts at `if (showUnlockDialog) {` and ends before `@Composable\nfun PrepaymentHeroCard`
dialog_start_idx = content.find('if (showUnlockDialog) {')
dialog_end_idx = content.find('}\n\n@Composable\nfun PrepaymentHeroCard')

if dialog_start_idx != -1 and dialog_end_idx != -1:
    old_dialog = content[dialog_start_idx:dialog_end_idx+1]
    
    new_dialog = """if (showUnlockDialog) {
        val dialogContext = androidx.compose.ui.platform.LocalContext.current
        com.loanmaster.pro.core.ui.PremiumUnlockDialog(
            onDismiss = { showUnlockDialog = false },
            onUnlockSuccessful = { 
                isAiUnlocked = true 
                com.loanmaster.pro.core.managers.PremiumManager(dialogContext).unlockPermanent()
            }
        )
    }"""
    
    content = content.replace(old_dialog, new_dialog)

    with open("app/src/main/java/com/loanmaster/pro/feature/prepayment/PrepaymentScreen.kt", "w") as f:
        f.write(content)
