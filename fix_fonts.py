import re
import os

files_to_modify = []
for root, dirs, files in os.walk('app/src/main/java/com/loanmaster/pro/'):
    for file in files:
        if file.endswith('.kt'):
            files_to_modify.append(os.path.join(root, file))

for file_path in files_to_modify:
    with open(file_path, 'r') as f:
        content = f.read()
    original_content = content
    content = content.replace('fontSize = 14.sp', 'fontSize = LoanMasterTheme.typography.label.fontSize')
    content = content.replace('fontSize = 38.sp,', 'fontSize = LoanMasterTheme.typography.display.fontSize,')

    if content != original_content:
        with open(file_path, 'w') as f:
            f.write(content)
        print(f"Updated {file_path}")
