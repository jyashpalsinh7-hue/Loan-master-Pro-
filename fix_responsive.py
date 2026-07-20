import sys

filename = 'app/src/main/java/com/loanmaster/pro/core/responsive/Responsive.kt'
with open(filename, 'r') as f:
    content = f.read()

target = 'Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.TopCenter) {'
replacement = 'Box(modifier = Modifier.fillMaxSize().safeDrawingPadding(), contentAlignment = androidx.compose.ui.Alignment.TopCenter) {'

if target in content:
    content = content.replace(target, replacement)
    with open(filename, 'w') as f:
        f.write(content)
    print("Done")
else:
    print("Target not found")
