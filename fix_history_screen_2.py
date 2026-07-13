import os

file_path = "app/src/main/java/com/loanmaster/pro/feature/history/HistoryScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

# Fix formatInr
content = content.replace("formatInr(", "formatMoney(")

# Fix HistoryItemCard signature
content = content.replace("isSelected: Boolean,", "isSelected: Boolean = false,")
content = content.replace("isMultiSelectMode: Boolean,", "isMultiSelectMode: Boolean = false,")
content = content.replace("onLongClick: () -> Unit,", "onLongClick: () -> Unit = {},")

with open(file_path, "w") as f:
    f.write(content)

print("Done")
