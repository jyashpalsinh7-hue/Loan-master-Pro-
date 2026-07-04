import re

with open("app/src/main/java/com/loanmaster/pro/FdCalculatorViewModel.kt", "r") as f:
    content = f.read()

content = content.replace("package com.loanmaster.pro", "package com.loanmaster.pro\n\nimport com.loanmaster.pro.model.*")

start_idx = content.find("data class FdYearBreakdown(")
end_idx = content.find("enum class CompoundingFrequency")

if start_idx != -1 and end_idx != -1:
    content = content[:start_idx] + content[end_idx:]

with open("app/src/main/java/com/loanmaster/pro/FdCalculatorViewModel.kt", "w") as f:
    f.write(content)
