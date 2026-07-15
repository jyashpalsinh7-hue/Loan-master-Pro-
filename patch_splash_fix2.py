import sys, re

file_path = "app/src/main/java/com/loanmaster/pro/feature/splash/SplashScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace("1(size.width * 0.03f)", "16.dp.toPx()")
content = content.replace("1(size.width * 0.02f)", "14.dp.toPx()")
content = content.replace("2(size.width * 0.02f)", "24.dp.toPx()")
content = content.replace("3(size.width * 0.02f)", "34.dp.toPx()")

# Any other malformed parts?
# (size.width * 0.07f) was 14.dp.toPx() originally in the script. Wait, I explicitly replaced 14.dp.toPx() earlier? 
# content = content.replace("14.dp.toPx()", "(size.width * 0.07f)") in patch_splash.py
# Let's just fix the remaining syntax errors.

with open(file_path, "w") as f:
    f.write(content)
print("Patched splash screen fix 2")
