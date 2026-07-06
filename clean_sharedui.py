import os

filepath = "app/src/main/java/com/loanmaster/pro/core/ui/SharedUI.kt"
with open(filepath, "r") as f:
    content = f.read()

import re

# Remove moneyFormat
content = re.sub(r'val moneyFormat = .*?\n\s+maximumFractionDigits = 0\n\}', '', content, flags=re.DOTALL)
content = re.sub(r'private val moneyFormat = .*?\n\s+maximumFractionDigits = 0\n\}', '', content, flags=re.DOTALL)

# Remove formatMoney function
# fun formatMoney(amt: Double): String {
#     if (amt <= 0) return "₹0"
#     return moneyFormat.format(amt)
# }
content = re.sub(r'fun CurrencyFormatter\.formatMoney\(amt: Double\): String \{.*?\n\}', '', content, flags=re.DOTALL)
content = re.sub(r'import java.text.NumberFormat\nimport java.util.Locale\n', '', content)

with open(filepath, "w") as f:
    f.write(content)
