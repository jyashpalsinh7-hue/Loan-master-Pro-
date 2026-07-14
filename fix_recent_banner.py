import os

file_path = "app/src/main/java/com/loanmaster/pro/feature/home/HomeScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

start_str = "@Composable\nfun RecentCalculationsBanner("
end_str = "    }\n}\n"

start_idx = content.find(start_str)
end_idx = content.find(end_str, start_idx) + len(end_str)

old_method = content[start_idx:end_idx]

new_method = """@Composable
fun RecentCalculationsBanner(
    historyItems: List<CalculationHistory>, 
    onNavigateToHistory: () -> Unit,
    onNavigateToCalculator: (CalculationHistory) -> Unit
) {
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
            ScrollingTitleText(
                if (historyItems.isEmpty()) "No history available yet" else "${historyItems.size} calculations saved", 
                color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize
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

content = content[:start_idx] + new_method + content[end_idx:]

with open(file_path, "w") as f:
    f.write(content)

print("Done")
