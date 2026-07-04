import re

with open("app/src/main/java/com/loanmaster/pro/EmiCalculatorViewModel.kt", "r") as f:
    content = f.read()

# Remove the bad imports at the top
content = content.replace("import androidx.lifecycle.ViewModelProvider\nimport androidx.lifecycle.viewModelScope\nimport kotlinx.coroutines.launch\nimport delay\n", "")

# Add them after package com.loanmaster.pro
new_imports = """
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
"""
content = content.replace("package com.loanmaster.pro", "package com.loanmaster.pro\n" + new_imports)

with open("app/src/main/java/com/loanmaster/pro/EmiCalculatorViewModel.kt", "w") as f:
    f.write(content)

