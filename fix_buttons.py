with open('app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt', 'r') as f:
    content = f.read()

# For Salaried / Self-Employed Row
# We have a Row with modifier ... heightIn(min = 84.dp)
# Let's keep that at 84.dp since they wanted it bigger.

# For Action buttons, they also got 84.dp. We can change them to 56.dp or 64.dp.
content = content.replace('.heightIn(min = 84.dp)', '.heightIn(min = 64.dp)')

with open('app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt', 'w') as f:
    f.write(content)
