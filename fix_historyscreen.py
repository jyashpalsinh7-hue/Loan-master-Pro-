import os

filepath = 'app/src/main/java/com/loanmaster/pro/feature/history/HistoryScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

target = "            Column(modifier = Modifier.background(BackgroundDark)) {"
replacement = "            Column(modifier = Modifier.background(BackgroundDark).statusBarsPadding()) {"

if target in content:
    content = content.replace(target, replacement)
    with open(filepath, 'w') as f:
        f.write(content)
    print("Fixed HistoryScreen.kt")
else:
    print("Target not found in HistoryScreen.kt")
