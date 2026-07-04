import re
with open("app/src/main/java/com/loanmaster/pro/AppNavigation.kt", "r") as f:
    content = f.read()

content = content.replace("@Composable\n@Composable\nfun AppNavigation", "@Composable\nfun AppNavigation")
content = content.replace("@Composable\n\n@Composable\nfun AppNavigation", "@Composable\nfun AppNavigation")

with open("app/src/main/java/com/loanmaster/pro/AppNavigation.kt", "w") as f:
    f.write(content)
