import sys

file_path = "app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

target = """                        onWatchAdClick = { 
                            activity?.let { act ->
                                com.loanmaster.pro.core.ads.RewardedAdManager.showAd(act) {
                                    intelligenceViewModel.onTemporaryUnlockEarned()
                                }
                            }
                        },"""

replacement = """                        onWatchAdClick = { 
                            if (activity != null) {
                                com.loanmaster.pro.core.ads.RewardedAdManager.showAd(activity) {
                                    intelligenceViewModel.onTemporaryUnlockEarned()
                                }
                            }
                        },"""

if target in content:
    content = content.replace(target, replacement)
    with open(file_path, "w") as f:
        f.write(content)
    print("Patched activity if")
else:
    print("Target not found")
