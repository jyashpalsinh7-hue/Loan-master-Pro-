import os

file_path = "app/src/main/java/com/loanmaster/pro/feature/home/HomeScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

target = """    val borderColor by androidx.compose.animation.animateColorAsState(targetValue = if (isFocused) AccentBlue else CardStroke)
    val glowAlpha by androidx.compose.animation.core.animateFloatAsState(targetValue = if (isFocused) 0.5f else 0f)
    
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = LoanMasterTheme.spacing.md),
        horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Search Bar
        Row(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = LoanMasterTheme.components.buttonHeight)
                .shadow(if (isFocused) LoanMasterTheme.spacing.sm else 0.dp, RoundedCornerShape(LoanMasterTheme.components.cardRadius), spotColor = AccentBlue.copy(alpha = glowAlpha))
                .clip(RoundedCornerShape(LoanMasterTheme.components.cardRadius))
                .background(SurfaceDark)
                .border(1.dp, borderColor, RoundedCornerShape(LoanMasterTheme.components.cardRadius))"""

replacement = """    val borderColor by androidx.compose.animation.animateColorAsState(targetValue = if (isFocused) AccentBlue else CardStroke)
    val borderWidth by androidx.compose.animation.core.animateDpAsState(targetValue = if (isFocused) 2.dp else 1.dp)
    val glowAlpha by androidx.compose.animation.core.animateFloatAsState(targetValue = if (isFocused) 0.5f else 0f)
    
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = LoanMasterTheme.spacing.md),
        horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Search Bar
        Row(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = LoanMasterTheme.components.buttonHeight)
                .shadow(if (isFocused) LoanMasterTheme.spacing.sm else 0.dp, RoundedCornerShape(LoanMasterTheme.components.cardRadius), spotColor = AccentBlue.copy(alpha = glowAlpha))
                .clip(RoundedCornerShape(LoanMasterTheme.components.cardRadius))
                .background(SurfaceDark)
                .border(borderWidth, borderColor, RoundedCornerShape(LoanMasterTheme.components.cardRadius))"""

content = content.replace(target, replacement)

with open(file_path, "w") as f:
    f.write(content)

print("Done")
