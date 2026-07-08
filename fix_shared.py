import re
with open('app/src/main/java/com/loanmaster/pro/core/ui/SharedUI.kt', 'r') as f:
    content = f.read()

content = content.replace('errorMessage: String? = null,', 'errorMessage: String? = null,\n    isNumeric: Boolean = true,')
content = content.replace('if (value.isNotEmpty() && value != "." && value.toDoubleOrNull() == null) {', 'if (isNumeric && value.isNotEmpty() && value != "." && value.toDoubleOrNull() == null) {')

with open('app/src/main/java/com/loanmaster/pro/core/ui/SharedUI.kt', 'w') as f:
    f.write(content)
