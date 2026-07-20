import sys

filename = 'app/src/main/java/com/loanmaster/pro/feature/splash/SplashScreen.kt'
with open(filename, 'r') as f:
    content = f.read()

content = content.replace('.width(66.dp)', '.widthIn(min = 66.dp)')
content = content.replace('.width(8.dp)', '.widthIn(min = 8.dp)')

with open(filename, 'w') as f:
    f.write(content)
