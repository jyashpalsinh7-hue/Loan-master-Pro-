import sys

file_path = "app/src/main/java/com/loanmaster/pro/MainActivity.kt"
with open(file_path, "r") as f:
    content = f.read()

target = """        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this) {}"""

replacement = """        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this) {
            android.util.Log.d("AdMob", "AdMob initialized")
        }"""

if target in content:
    content = content.replace(target, replacement)
    with open(file_path, "w") as f:
        f.write(content)
    print("Patched MainActivity")
else:
    print("Target not found")
