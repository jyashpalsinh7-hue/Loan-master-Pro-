import re

with open("app/src/main/java/com/loanmaster/pro/LoanComparisonScreen.kt", "r") as f:
    content = f.read()

content = content.replace("package com.loanmaster.pro", "package com.loanmaster.pro\n\nimport com.loanmaster.pro.model.*")
content = content.replace("loanAState.color", "Color(0xFF3B82F6)")
content = content.replace("loanBState.color", "Color(0xFF10B981)")
content = content.replace("val loan = loans[i]", "val loan = loans[i]\n                        val loanColor = if (loan.id == \"A\") Color(0xFF3B82F6) else Color(0xFF10B981)")

content = content.replace("loan.color", "loanColor")

with open("app/src/main/java/com/loanmaster/pro/LoanComparisonScreen.kt", "w") as f:
    f.write(content)
