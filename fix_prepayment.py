import os

filepath = 'app/src/main/java/com/loanmaster/pro/feature/prepayment/PrepaymentScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

target = "\n@Composable\nfun PrepaymentHeroCard"
replacement = "\n    }\n}\n@Composable\nfun PrepaymentHeroCard"
if target in content:
    content = content.replace(target, replacement)

with open(filepath, 'w') as f:
    f.write(content)
