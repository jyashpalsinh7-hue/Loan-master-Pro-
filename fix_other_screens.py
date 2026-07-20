import os

files = [
    "app/src/main/java/com/loanmaster/pro/feature/rd/RdScreen.kt",
    "app/src/main/java/com/loanmaster/pro/feature/sip/SipScreen.kt",
    "app/src/main/java/com/loanmaster/pro/feature/compare/CompareScreen.kt"
]

for file in files:
    with open(file, "r") as f:
        content = f.read()

    # Find where context is defined or define it
    if "val context = androidx.compose.ui.platform.LocalContext.current" not in content:
        # It's probably already there, but just to be safe
        pass

    content = content.replace(
        "onUnlockSuccessful = { isPremiumUnlocked = true }",
        """onUnlockSuccessful = { 
                isPremiumUnlocked = true 
                val context = androidx.compose.ui.platform.LocalContext.current
                com.loanmaster.pro.core.managers.PremiumManager(context).unlockPermanent()
            }"""
    )

    with open(file, "w") as f:
        f.write(content)
