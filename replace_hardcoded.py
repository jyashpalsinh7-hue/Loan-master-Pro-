import os
import re

files_to_modify = []
for root, dirs, files in os.walk('app/src/main/java/com/loanmaster/pro/'):
    for file in files:
        if file.endswith('.kt'):
            files_to_modify.append(os.path.join(root, file))

for file_path in files_to_modify:
    with open(file_path, 'r') as f:
        content = f.read()

    original_content = content
    content = re.sub(r'Modifier\.size\(\s*18\.dp\s*\)', 'Modifier.size(LoanMasterTheme.components.iconSmall)', content)
    content = re.sub(r'Modifier\.size\(\s*24\.dp\s*\)', 'Modifier.size(LoanMasterTheme.components.iconMedium)', content)
    content = re.sub(r'Modifier\.size\(\s*28\.dp\s*\)', 'Modifier.size(LoanMasterTheme.components.iconMedium)', content)
    content = re.sub(r'Modifier\.size\(\s*32\.dp\s*\)', 'Modifier.size(LoanMasterTheme.components.iconLarge)', content)
    content = re.sub(r'Modifier\.size\(\s*36\.dp\s*\)', 'Modifier.size(LoanMasterTheme.components.iconLarge)', content)
    content = re.sub(r'Modifier\.size\(\s*80\.dp\s*\)', 'Modifier.size(LoanMasterTheme.components.iconLarge * 2)', content)

    if content != original_content:
        with open(file_path, 'w') as f:
            f.write(content)
        print(f"Updated {file_path}")

