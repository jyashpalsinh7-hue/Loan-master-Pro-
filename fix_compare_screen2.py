import os

file_path = "app/src/main/java/com/loanmaster/pro/feature/compare/CompareScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace(".androidx.compose.ui.focus.onFocusChanged", ".onFocusChanged")

with open(file_path, "w") as f:
    f.write(content)

print("Done")
