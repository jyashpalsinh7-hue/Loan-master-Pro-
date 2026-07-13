import os

file_path = "app/src/main/java/com/loanmaster/pro/feature/history/HistoryScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

# Replace hardcoded font sizes with LoanMasterTheme typography where appropriate
content = content.replace("fontSize = 16.sp", "fontSize = LoanMasterTheme.typography.body.fontSize")
content = content.replace("fontSize = 12.sp", "fontSize = LoanMasterTheme.typography.label.fontSize")
content = content.replace("fontSize = 13.sp", "fontSize = LoanMasterTheme.typography.label.fontSize")
content = content.replace("fontSize = 14.sp", "fontSize = LoanMasterTheme.typography.body.fontSize")
content = content.replace("fontSize = 20.sp", "fontSize = LoanMasterTheme.typography.title.fontSize")

with open(file_path, "w") as f:
    f.write(content)

print("Done")
