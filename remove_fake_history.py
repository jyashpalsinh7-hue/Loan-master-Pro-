import re

with open("app/src/main/java/com/loanmaster/pro/MainActivity.kt", "r") as f:
    content = f.read()

content = re.sub(r"// FAKE HISTORY FOR TESTING.*?LaunchedEffect\(Unit\) \{.*?\}.*?AppNavigation\(", "AppNavigation(", content, flags=re.DOTALL)

with open("app/src/main/java/com/loanmaster/pro/MainActivity.kt", "w") as f:
    f.write(content)
