import os

# 1. Fix SharedUI.kt
file_path = "app/src/main/java/com/loanmaster/pro/core/ui/SharedUI.kt"
with open(file_path, "r") as f:
    content = f.read()

if "import androidx.compose.ui.focus.onFocusChanged" not in content:
    content = content.replace("import androidx.compose.ui.unit.sp", "import androidx.compose.ui.unit.sp\nimport androidx.compose.ui.focus.onFocusChanged")

with open(file_path, "w") as f:
    f.write(content)

# 2. Fix PrepaymentScreen.kt
file_path = "app/src/main/java/com/loanmaster/pro/feature/prepayment/PrepaymentScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

if "import androidx.compose.ui.focus.onFocusChanged" not in content:
    content = content.replace("import androidx.compose.ui.unit.sp", "import androidx.compose.ui.unit.sp\nimport androidx.compose.ui.focus.onFocusChanged")

with open(file_path, "w") as f:
    f.write(content)

# 3. Fix SipScreen.kt
file_path = "app/src/main/java/com/loanmaster/pro/feature/sip/SipScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

if "import androidx.compose.ui.focus.onFocusChanged" not in content:
    content = content.replace("import androidx.compose.ui.unit.sp", "import androidx.compose.ui.unit.sp\nimport androidx.compose.ui.focus.onFocusChanged")

content = content.replace(".androidx.compose.ui.focus.onFocusChanged", ".onFocusChanged")

with open(file_path, "w") as f:
    f.write(content)

print("Done")
