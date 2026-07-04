import re

with open("app/src/main/java/com/loanmaster/pro/AppNavigation.kt", "r") as f:
    content = f.read()

content = content.replace("historyCount = historyList.size,", "historyItems = historyList,")

with open("app/src/main/java/com/loanmaster/pro/AppNavigation.kt", "w") as f:
    f.write(content)
