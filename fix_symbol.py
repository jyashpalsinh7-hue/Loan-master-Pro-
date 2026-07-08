import os

def replace_in_files(directory, old_str, new_str):
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith(".kt"):
                filepath = os.path.join(root, file)
                with open(filepath, 'r') as f:
                    content = f.read()
                
                if old_str in content:
                    content = content.replace(old_str, new_str)
                    with open(filepath, 'w') as f:
                        f.write(content)

replace_in_files('app/src/main/java', 
                 'com.loanmaster.pro.core.formatter.currentCurrencySymbol', 
                 'com.loanmaster.pro.LocalCurrencySymbol.current')
