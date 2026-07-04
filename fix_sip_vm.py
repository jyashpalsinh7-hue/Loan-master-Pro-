import re

with open("app/src/main/java/com/loanmaster/pro/SipCalculatorViewModel.kt", "r") as f:
    content = f.read()

content = content.replace("package com.loanmaster.pro", "package com.loanmaster.pro\n\nimport com.loanmaster.pro.model.*")

start_idx = content.find("data class SipYearBreakdown(")
end_idx = content.find("class SipCalculatorViewModel")

if start_idx != -1 and end_idx != -1:
    content = content[:start_idx] + content[end_idx:]

with open("app/src/main/java/com/loanmaster/pro/SipCalculatorViewModel.kt", "w") as f:
    f.write(content)
