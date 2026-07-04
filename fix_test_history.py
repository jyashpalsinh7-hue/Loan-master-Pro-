import re

with open("app/src/main/java/com/loanmaster/pro/MainActivity.kt", "r") as f:
    content = f.read()

content = content.replace("import androidx.activity.compose.setContent", "import androidx.activity.compose.setContent\nimport androidx.compose.runtime.LaunchedEffect")
    
with open("app/src/main/java/com/loanmaster/pro/MainActivity.kt", "w") as f:
    f.write(content)
