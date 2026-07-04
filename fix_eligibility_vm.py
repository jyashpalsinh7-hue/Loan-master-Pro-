import re

with open("app/src/main/java/com/loanmaster/pro/LoanEligibilityViewModel.kt", "r") as f:
    content = f.read()

content = content.replace("package com.loanmaster.pro", "package com.loanmaster.pro\n\nimport com.loanmaster.pro.model.*")
content = content.replace("import androidx.compose.ui.graphics.Color\n", "")

# Remove verdictColor
content = re.sub(r'\s*val verdictColor: Color = [^\n]+,', '', content)
content = content.replace("val verdictColor: Color", "val verdictColorString: String") # We won't even need color here actually.
content = re.sub(r'verdictColor = Color\([^)]+\),?', '', content)

# Replace SmartAlert instances
content = content.replace('SmartAlert("Positive", "Credit Score is Excellent, lowering interest burden.", Color(0xFF4CAF50))', 'SmartAlert(AlertType.POSITIVE, "Credit Score is Excellent, lowering interest burden.")')
content = content.replace('SmartAlert("Positive", "Credit score is good, favorable for approvals.", Color(0xFF4CAF50))', 'SmartAlert(AlertType.POSITIVE, "Credit score is good, favorable for approvals.")')
content = content.replace('SmartAlert("Warning", "Fair credit score might lead to higher rates.", Color(0xFFFF9800))', 'SmartAlert(AlertType.WARNING, "Fair credit score might lead to higher rates.")')
content = content.replace('SmartAlert("Critical", "Poor credit score makes approval very difficult.", Color(0xFFF44336))', 'SmartAlert(AlertType.CRITICAL, "Poor credit score makes approval very difficult.")')

content = content.replace('SmartAlert("Positive", "Low existing debt, great EMI capacity.", Color(0xFF4CAF50))', 'SmartAlert(AlertType.POSITIVE, "Low existing debt, great EMI capacity.")')
content = content.replace('SmartAlert("Critical", "Existing obligations exceed safe limits.", Color(0xFFF44336))', 'SmartAlert(AlertType.CRITICAL, "Existing obligations exceed safe limits.")')
content = content.replace('SmartAlert("Warning", "High existing debt ratio limits your borrowing.", Color(0xFFFF9800))', 'SmartAlert(AlertType.WARNING, "High existing debt ratio limits your borrowing.")')

content = content.replace('SmartAlert("Positive", "Short tenure saves interest cost.", Color(0xFF4CAF50))', 'SmartAlert(AlertType.POSITIVE, "Short tenure saves interest cost.")')
content = content.replace('SmartAlert("Critical", "Tenure is exceedingly long, high interest burden.", Color(0xFFF44336))', 'SmartAlert(AlertType.CRITICAL, "Tenure is exceedingly long, high interest burden.")')

with open("app/src/main/java/com/loanmaster/pro/LoanEligibilityViewModel.kt", "w") as f:
    f.write(content)
