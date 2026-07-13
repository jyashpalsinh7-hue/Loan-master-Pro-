import os

file_path = "app/src/main/java/com/loanmaster/pro/feature/currency/CurrencyScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

target = """    val borderColor by animateColorAsState(
        targetValue = if (isFocused) CurrSecondaryAccent else CurrCardStrokeColor,
        animationSpec = tween(durationMillis = 300)
    )
    val backgroundColor by animateColorAsState(
        targetValue = if (isFocused) CurrSecondaryAccent.copy(alpha = 0.05f) else Color.Transparent,
        animationSpec = tween(durationMillis = 300)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(LoanMasterTheme.spacing.md))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(LoanMasterTheme.spacing.md))"""

replacement = """    val borderColor by animateColorAsState(
        targetValue = if (isFocused) CurrSecondaryAccent else CurrCardStrokeColor,
        animationSpec = tween(durationMillis = 300)
    )
    val borderWidth by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isFocused) 2.dp else 1.dp,
        animationSpec = tween(durationMillis = 300)
    )
    val backgroundColor by animateColorAsState(
        targetValue = if (isFocused) CurrSecondaryAccent.copy(alpha = 0.05f) else Color.Transparent,
        animationSpec = tween(durationMillis = 300)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(LoanMasterTheme.spacing.md))
            .background(backgroundColor)
            .border(borderWidth, borderColor, RoundedCornerShape(LoanMasterTheme.spacing.md))"""

content = content.replace(target, replacement)

with open(file_path, "w") as f:
    f.write(content)

print("Done")
