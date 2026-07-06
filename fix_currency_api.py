path_ui = "app/src/main/java/com/loanmaster/pro/feature/currency/CurrencyUiState.kt"
path_vm = "app/src/main/java/com/loanmaster/pro/feature/currency/CurrencyViewModel.kt"

with open(path_ui, "r") as f:
    ui_content = f.read()

# Extract Retrofit interfaces
import re
match = re.search(r'interface FrankfurterApi[\s\S]*\}', ui_content)
if match:
    apis = match.group(0)
    ui_content = ui_content.replace(apis, "")
    
    with open(path_ui, "w") as f:
        f.write(ui_content)
    
    with open(path_vm, "r") as f:
        vm_content = f.read()
    
    if "interface FrankfurterApi" not in vm_content:
        # insert it after imports
        vm_content = vm_content.replace("class CurrencyViewModel", apis + "\n\nclass CurrencyViewModel")
        
        if "import retrofit2.http.*" not in vm_content:
            vm_content = vm_content.replace("package com.loanmaster.pro.feature.currency\n", "package com.loanmaster.pro.feature.currency\nimport retrofit2.http.*\n")

        with open(path_vm, "w") as f:
            f.write(vm_content)
