with open('app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt', 'r') as f:
    content = f.read()

# Make Self-Employed text bigger
content = content.replace('Text("Self-Employed / Business", color = if (!isSalaried) textColor else textSecondary, style = LoanMasterTheme.typography.label', 'Text("Self-Employed / Business", color = if (!isSalaried) textColor else textSecondary, style = LoanMasterTheme.typography.body')

# Make Credit Score Title bigger
content = content.replace('Text(title, color = textColor, style = LoanMasterTheme.typography.label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)', 'Text(title, color = textColor, style = LoanMasterTheme.typography.body, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)')

# Make Credit Score Range slightly bigger
content = content.replace('Text(range, color = if (isSelected) textColor else textSecondary, style = LoanMasterTheme.typography.label)', 'Text(range, color = if (isSelected) textColor else textSecondary, style = LoanMasterTheme.typography.body)')

# Metric Cards height
content = content.replace('.heightIn(min = 110.dp)', '.heightIn(min = 124.dp)')

with open('app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt', 'w') as f:
    f.write(content)
