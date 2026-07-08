import os
import re

def process_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    if 'com.loanmaster.pro.core.formatter.formatMoneyComposable' in content:
        content = content.replace('com.loanmaster.pro.core.formatter.formatMoneyComposable(', 'com.loanmaster.pro.core.formatter.formatMoney(')
        
        with open(filepath, 'w') as f:
            f.write(content)

for root, dirs, files in os.walk("app/src/main/java"):
    for file in files:
        if file.endswith(".kt"):
            process_file(os.path.join(root, file))
