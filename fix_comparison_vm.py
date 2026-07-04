import re

with open("app/src/main/java/com/loanmaster/pro/LoanComparisonViewModel.kt", "r") as f:
    content = f.read()

content = content.replace("package com.loanmaster.pro", "package com.loanmaster.pro\n\nimport com.loanmaster.pro.model.*")
content = content.replace("import androidx.compose.ui.graphics.Color\n", "")

content = re.sub(r',\s*val color: Color', '', content)
content = content.replace('Color(0xFF3B82F6)', '')
content = content.replace('Color(0xFF10B981)', '')
content = content.replace(', )', ')')
content = content.replace('val loanA: LoanOptionState = LoanOptionState("A", "Loan A", "1000000", "8.5", "5", "0" )', 'val loanA: LoanOptionState = LoanOptionState("A", "Loan A", "1000000", "8.5", "5", "0")')
content = content.replace('val loanB: LoanOptionState = LoanOptionState("B", "Loan B", "1000000", "8.0", "5", "0" )', 'val loanB: LoanOptionState = LoanOptionState("B", "Loan B", "1000000", "8.0", "5", "0")')
content = content.replace('val loanA: LoanOptionState = LoanOptionState("A", "Loan A", "1000000", "8.5", "5", "0", )', 'val loanA: LoanOptionState = LoanOptionState("A", "Loan A", "1000000", "8.5", "5", "0")')
content = content.replace('val loanB: LoanOptionState = LoanOptionState("B", "Loan B", "1000000", "8.0", "5", "0", )', 'val loanB: LoanOptionState = LoanOptionState("B", "Loan B", "1000000", "8.0", "5", "0")')

with open("app/src/main/java/com/loanmaster/pro/LoanComparisonViewModel.kt", "w") as f:
    f.write(content)
