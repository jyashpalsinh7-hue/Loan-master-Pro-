import os

filepath = 'app/src/main/java/com/loanmaster/pro/core/ui/SharedUI.kt'
with open(filepath, 'r') as f:
    content = f.read()

target = "import androidx.compose.foundation.layout.fillMaxWidth"
replacement = "import androidx.compose.foundation.layout.fillMaxWidth\nimport androidx.compose.foundation.layout.safeDrawingPadding"

if target in content:
    content = content.replace(target, replacement)
else:
    # just put it at top
    content = content.replace("import androidx.compose.foundation.layout.Box", "import androidx.compose.foundation.layout.Box\nimport androidx.compose.foundation.layout.safeDrawingPadding")

with open(filepath, 'w') as f:
    f.write(content)
