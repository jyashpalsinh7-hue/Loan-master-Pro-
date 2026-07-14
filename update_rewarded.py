import sys

file_path = "app/src/main/java/com/loanmaster/pro/core/ads/RewardedAdManager.kt"
with open(file_path, "r") as f:
    content = f.read()

target = """        } ?: run {
            Log.d(TAG, "The rewarded ad wasn't ready yet.")
            onRewardEarned()
            loadAd(activity)
        }"""

replacement = """        } ?: run {
            Log.d(TAG, "The rewarded ad wasn't ready yet.")
            android.widget.Toast.makeText(activity, "Ad is still loading. Please try again in a few seconds.", android.widget.Toast.LENGTH_SHORT).show()
            loadAd(activity)
        }"""

if target in content:
    content = content.replace(target, replacement)
    with open(file_path, "w") as f:
        f.write(content)
    print("RewardedAdManager updated successfully")
else:
    print("Target not found in RewardedAdManager")
