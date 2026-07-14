import sys

file_path = "app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

target = """                        onWatchAdClick = { intelligenceViewModel.unlockTemporary() },"""

replacement = """                        // FIX: Use real RewardedAdManager
                        onWatchAdClick = { 
                            com.loanmaster.pro.core.ads.RewardedAdManager.showAd(activity) {
                                intelligenceViewModel.onTemporaryUnlockEarned()
                            }
                        },"""

if target in content:
    content = content.replace(target, replacement)
    with open(file_path, "w") as f:
        f.write(content)
    print("Patched UI")
else:
    print("Not found")
