import os
import re

def process_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    if '@Composable' in content and 'EmiCalculator' not in filepath and 'CurrencyFormatter.kt' not in filepath:
        # replace formatMoney( with formatMoneyComposable(
        content = content.replace('formatMoney(', 'com.loanmaster.pro.core.formatter.formatMoneyComposable(')
        
        with open(filepath, 'w') as f:
            f.write(content)

for root, dirs, files in os.walk("app/src/main/java/com/loanmaster/pro/feature"):
    for file in files:
        if file.endswith(".kt"):
            process_file(os.path.join(root, file))
for root, dirs, files in os.walk("app/src/main/java/com/loanmaster/pro/core/ui"):
    for file in files:
        if file.endswith(".kt"):
            process_file(os.path.join(root, file))
