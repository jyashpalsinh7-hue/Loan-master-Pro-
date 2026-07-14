import os

file_path = "app/src/main/java/com/loanmaster/pro/MainActivity.kt"
with open(file_path, "r") as f:
    content = f.read()

target = """        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this) {}"""

replacement = """        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this) {}
        
        // Load rewarded ad
        com.loanmaster.pro.core.ads.RewardedAdManager.loadAd(this)"""

if "RewardedAdManager.loadAd(this)" not in content:
    content = content.replace(target, replacement)
    with open(file_path, "w") as f:
        f.write(content)
    print("Done")
else:
    print("Already there")
