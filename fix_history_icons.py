import os

file_path = "app/src/main/java/com/loanmaster/pro/feature/history/HistoryScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

# 1. Add iconColor to CardData
content = content.replace(
    "val icon: ImageVector,",
    "val icon: ImageVector,\n    val iconColor: Color,"
)

# 2. Update EMI CardData
content = content.replace(
    """CardData(
                calculatorName = "EMI Calculator",
                icon = Icons.Rounded.Calculate,""",
    """CardData(
                calculatorName = "EMI Calculator",
                icon = Icons.Rounded.Calculate,
                iconColor = Color(0xFF2563EB),"""
)

# 3. Update SIP CardData
content = content.replace(
    """CardData(
                calculatorName = "SIP Calculator",
                icon = Icons.AutoMirrored.Rounded.TrendingUp,""",
    """CardData(
                calculatorName = "SIP Calculator",
                icon = Icons.AutoMirrored.Rounded.TrendingUp,
                iconColor = Color(0xFF43A047),"""
)

# 4. Update FD CardData
content = content.replace(
    """CardData(
                calculatorName = "FD Calculator",
                icon = Icons.Rounded.AccountBalance,""",
    """CardData(
                calculatorName = "FD Calculator",
                icon = Icons.Rounded.Savings,
                iconColor = Color(0xFFD81B60),"""
)

# 5. Update RD CardData
content = content.replace(
    """CardData(
                calculatorName = "RD Calculator",
                icon = Icons.Rounded.Savings,""",
    """CardData(
                calculatorName = "RD Calculator",
                icon = Icons.Rounded.CalendarToday,
                iconColor = Color(0xFFFF9800),"""
)

# 6. Update Prepayment CardData
content = content.replace(
    """CardData(
                calculatorName = "Loan Prepayment",
                icon = Icons.Rounded.AttachMoney,""",
    """CardData(
                calculatorName = "Loan Prepayment",
                icon = Icons.Rounded.EditNote,
                iconColor = Color(0xFF5E35B1),"""
)

# 7. Update GST CardData
content = content.replace(
    """CardData(
                calculatorName = "GST Calculator",
                icon = Icons.AutoMirrored.Rounded.ReceiptLong,""",
    """CardData(
                calculatorName = "GST Calculator",
                icon = Icons.Rounded.Receipt,
                iconColor = Color(0xFFE53935),"""
)

# 8. Update fallback CardData
content = content.replace(
    """CardData(
                calculatorName = item.calculatorType,
                icon = Icons.Rounded.Calculate,""",
    """CardData(
                calculatorName = item.calculatorType,
                icon = Icons.Rounded.Calculate,
                iconColor = AccentYellow,"""
)

# 9. Update HistoryItemCard to use iconColor
content = content.replace(
    """Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(AccentYellow.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(cardData.icon, contentDescription = null, tint = AccentYellow, modifier = Modifier.size(24.dp))
                }""",
    """Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(cardData.iconColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(cardData.icon, contentDescription = null, tint = cardData.iconColor, modifier = Modifier.size(24.dp))
                }"""
)

with open(file_path, "w") as f:
    f.write(content)

print("Done")
