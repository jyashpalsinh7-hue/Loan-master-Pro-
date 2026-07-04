import re

with open("app/src/main/java/com/loanmaster/pro/EmiCalculatorScreen.kt", "r") as f:
    content = f.read()

content = content.replace("package com.loanmaster.pro", "package com.loanmaster.pro\n\nimport com.loanmaster.pro.model.*")

content = content.replace("alert.color", "when(alert.type) { AlertType.CRITICAL -> Color(0xFFF44336); AlertType.WARNING -> Color(0xFFFF9800); AlertType.POSITIVE -> Color(0xFF4CAF50); else -> Color.Gray }")
content = content.replace("alert.type", "alert.type.name")

with open("app/src/main/java/com/loanmaster/pro/EmiCalculatorScreen.kt", "w") as f:
    f.write(content)
