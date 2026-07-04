import re
file_path = 'app/src/main/java/com/loanmaster/pro/CurrencyConverterScreen.kt'
with open(file_path, 'r') as f:
    content = f.read()

content = content.replace('Modifier\n                .fillMaxWidth()\n                .height(200.dp)', 'Modifier\n                .fillMaxWidth()\n                .heightIn(min = 200.dp, max = 300.dp)')

with open(file_path, 'w') as f:
    f.write(content)
