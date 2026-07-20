import sys

filename = 'app/src/main/java/com/loanmaster/pro/core/ui/SharedUI.kt'
with open(filename, 'r') as f:
    content = f.read()

target = 'modifier = Modifier.fillMaxSize(),\n        contentAlignment = Alignment.TopCenter'
replacement = 'modifier = Modifier.fillMaxSize().safeDrawingPadding(),\n        contentAlignment = Alignment.TopCenter'

if target in content:
    content = content.replace(target, replacement)
    with open(filename, 'w') as f:
        f.write(content)
    print("Done")
else:
    print("Target not found")
