import os

filepath = 'app/src/main/java/com/loanmaster/pro/feature/currency/CurrencyScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

if content.endswith("    }\n}"):
    content = content[:-6]

target = "\n@Composable\nfun PremiumConversionCard"
replacement = "\n        }\n@Composable\nfun PremiumConversionCard"
if target in content:
    content = content.replace(target, replacement)

with open(filepath, 'w') as f:
    f.write(content)
