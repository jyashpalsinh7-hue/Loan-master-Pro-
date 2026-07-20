import os

filepath = 'app/src/main/java/com/loanmaster/pro/feature/fd/FdScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

target = "                animationTriggerState = maturityValue,\n                headerSection = { },"
replacement = "                animationTriggerState = maturityValue,"
content = content.replace(target, replacement)

with open(filepath, 'w') as f:
    f.write(content)
