import re

with open("app/src/main/java/com/loanmaster/pro/HomeScreen.kt", "r") as f:
    content = f.read()

# Replace historyCount with historyItems in the signature
content = re.sub(
    r"historyCount:\s*Int\s*=\s*0",
    "historyItems: List<CalculationHistory> = emptyList()",
    content
)

# Replace RecentCalculationsBanner(historyCount, onNavigateToHistory) with RecentCalculationsBanner(historyItems, onNavigateToHistory)
content = content.replace(
    "RecentCalculationsBanner(historyCount, onNavigateToHistory)",
    "RecentCalculationsBanner(historyItems, onNavigateToHistory)"
)

# Now rewrite RecentCalculationsBanner function
start_idx = content.find("@Composable\nfun RecentCalculationsBanner")
if start_idx != -1:
    end_idx = content.find("\nfun ", start_idx + 10)
    if end_idx == -1:
        end_idx = len(content)
    
    new_banner = """@Composable
fun RecentCalculationsBanner(historyItems: List<CalculationHistory>, onNavigateToHistory: () -> Unit) {
    if (historyItems.isEmpty()) {
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
                    "No history available yet", color = TextSecondary, fontSize = LoanMasterTheme.typography.label.fontSize
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
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.sm)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Recent History", color = TextPrimary, style = LoanMasterTheme.typography.body, fontWeight = FontWeight.Bold)
                TextButton(onClick = onNavigateToHistory) {
                    Text("View All", color = AccentBlue, style = LoanMasterTheme.typography.label)
                }
            }
            historyItems.take(3).forEach { item ->
                // Using HistoryItemCard
                HistoryItemCard(
                    item = item,
                    onItemClick = onNavigateToHistory,
                    onDeleteClick = {} // Won't delete from home screen
                )
            }
        }
    }
}
"""
    content = content[:start_idx] + new_banner

with open("app/src/main/java/com/loanmaster/pro/HomeScreen.kt", "w") as f:
    f.write(content)
