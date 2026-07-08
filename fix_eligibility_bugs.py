with open('app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt', 'r') as f:
    content = f.read()

# Fix back button
content = content.replace('.clickable { }', '.clickable { onBackClick() }', 1)

# Fix Salaried/Self-Employed text
content = content.replace('Text("Salaried", color = if (isSalaried) textColor else textSecondary, style = LoanMasterTheme.typography.body, fontWeight = if (isSalaried) FontWeight.Bold else FontWeight.Normal)', 
                          'Text("Salaried", color = if (isSalaried) textColor else textSecondary, style = LoanMasterTheme.typography.label, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = if (isSalaried) FontWeight.Bold else FontWeight.Normal)')

content = content.replace('Text("Self-Employed / Business", color = if (!isSalaried) textColor else textSecondary, style = LoanMasterTheme.typography.body, maxLines = 1, fontWeight = if (!isSalaried) FontWeight.Bold else FontWeight.Normal)',
                          'Text("Self-Employed", color = if (!isSalaried) textColor else textSecondary, style = LoanMasterTheme.typography.label, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = if (!isSalaried) FontWeight.Bold else FontWeight.Normal)')


# Fix Credit Score texts
content = content.replace('Text(title, color = textColor, style = LoanMasterTheme.typography.body, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)',
                          'Text(title, color = textColor, style = LoanMasterTheme.typography.label, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)')

content = content.replace('Text(range, color = if (isSelected) textColor else textSecondary, style = LoanMasterTheme.typography.body)',
                          'Text(range, color = if (isSelected) textColor else textSecondary, style = LoanMasterTheme.typography.label, maxLines = 1, overflow = TextOverflow.Ellipsis)')


with open('app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt', 'w') as f:
    f.write(content)
