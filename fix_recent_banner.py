with open('app/src/main/java/com/loanmaster/pro/HomeScreen.kt', 'r') as f:
    lines = f.readlines()

new_banner = """@Composable
fun RecentCalculationsBanner(historyItems: List<CalculationHistory>, onNavigateToHistory: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(LoanMasterTheme.components.cardRadius))
            .background(SurfaceDark)
            .border(1.dp, CardStroke, RoundedCornerShape(LoanMasterTheme.components.cardRadius))
            .clickable { onNavigateToHistory() }
            .padding(LoanMasterTheme.spacing.md)
            .testTag("recent_calc_banner"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.AccessTime,
            contentDescription = null,
            tint = AccentBlue,
            modifier = Modifier.size(LoanMasterTheme.components.iconLarge)
        )
        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.md))
        Column(modifier = Modifier.weight(1f, fill=false).padding(end=LoanMasterTheme.spacing.xs)) {
            ScrollingTitleText(
                "Recent Calculations", color = TextPrimary, fontSize = LoanMasterTheme.typography.body.fontSize, fontWeight = FontWeight.Bold
            )
            val descText = if (historyItems.isEmpty()) "No history available yet" else "${historyItems.size} recent calculation${if (historyItems.size > 1) "s" else ""}"
            ScrollingTitleText(
                descText, color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize
            )
        }
        Spacer(modifier = Modifier.widthIn(min = LoanMasterTheme.spacing.sm))
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(LoanMasterTheme.components.iconMedium)
        )
    }
}
"""

with open('app/src/main/java/com/loanmaster/pro/HomeScreen.kt', 'w') as f:
    f.writelines(lines[:595])
    f.write(new_banner)
    f.writelines(lines[658:])

print("Successfully replaced.")
