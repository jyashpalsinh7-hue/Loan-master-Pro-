import sys

file_path = "app/src/main/java/com/loanmaster/pro/feature/splash/SplashScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace("2(size.width * 0.03f)", "(size.width * 0.13f)")

with open(file_path, "w") as f:
    f.write(content)
print("Patched splash screen fix")
