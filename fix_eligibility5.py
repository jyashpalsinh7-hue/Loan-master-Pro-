with open('app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt', 'r') as f:
    content = f.read()

content = content.replace('Icon(Icons.Rounded.Work, contentDescription = null, tint = if (isSalaried) textColor else textSecondary, modifier = Modifier.size(LoanMasterTheme.spacing.md))', 'Icon(Icons.Rounded.Work, contentDescription = null, tint = if (isSalaried) textColor else textSecondary, modifier = Modifier.size(32.dp))')
content = content.replace('Icon(Icons.Rounded.Storefront, contentDescription = null, tint = if (!isSalaried) textColor else textSecondary, modifier = Modifier.size(LoanMasterTheme.spacing.md))', 'Icon(Icons.Rounded.Storefront, contentDescription = null, tint = if (!isSalaried) textColor else textSecondary, modifier = Modifier.size(32.dp))')

with open('app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt', 'w') as f:
    f.write(content)
