with open("app/src/main/java/com/loanmaster/pro/feature/currency/CurrencyUiState.kt", "r") as f:
    content = f.read()

content = content.replace("package com.loanmaster.pro.feature.currency", "package com.loanmaster.pro.feature.currency\nimport retrofit2.http.*\n")

with open("app/src/main/java/com/loanmaster/pro/feature/currency/CurrencyUiState.kt", "w") as f:
    f.write(content)

