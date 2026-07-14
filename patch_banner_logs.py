import sys

file_path = "app/src/main/java/com/loanmaster/pro/core/ui/AdMobBanner.kt"
with open(file_path, "r") as f:
    content = f.read()

target = """                            override fun onAdLoaded() {
                                Log.d("AdMobBanner", "Banner loaded")
                            }"""
replacement = target

if "Log.d(\"AdMobBanner\", \"Banner requested\")" not in content:
    target2 = """                        loadAd(AdRequest.Builder().build())"""
    replacement2 = """                        Log.d("AdMobBanner", "Banner requested")\n                        loadAd(AdRequest.Builder().build())"""
    content = content.replace(target2, replacement2)

with open(file_path, "w") as f:
    f.write(content)
print("Patched banner logs")
