import os

file_path = "app/src/main/java/com/loanmaster/pro/feature/settings/SettingsScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace("CardBackgroundLight", "SurfaceDark")

with open(file_path, "w") as f:
    f.write(content)
print("Done")
