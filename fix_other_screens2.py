import os
import re

files = [
    "app/src/main/java/com/loanmaster/pro/feature/rd/RdScreen.kt",
    "app/src/main/java/com/loanmaster/pro/feature/sip/SipScreen.kt",
    "app/src/main/java/com/loanmaster/pro/feature/compare/CompareScreen.kt"
]

for file in files:
    with open(file, "r") as f:
        content = f.read()

    # We need to pull LocalContext outside of onUnlockSuccessful
    content = content.replace(
        "onUnlockSuccessful = { \n                isPremiumUnlocked = true \n                val context = androidx.compose.ui.platform.LocalContext.current\n                com.loanmaster.pro.core.managers.PremiumManager(context).unlockPermanent()\n            }",
        "onUnlockSuccessful = { \n                isPremiumUnlocked = true \n                com.loanmaster.pro.core.managers.PremiumManager(dialogContext).unlockPermanent()\n            }"
    )

    content = content.replace(
        "com.loanmaster.pro.core.ui.PremiumUnlockDialog(",
        "val dialogContext = androidx.compose.ui.platform.LocalContext.current\n        com.loanmaster.pro.core.ui.PremiumUnlockDialog("
    )

    with open(file, "w") as f:
        f.write(content)
