import re

with open("app/src/main/java/com/loanmaster/pro/HomeScreen.kt", "r") as f:
    content = f.read()

# Replace historyCount with historyItems in HomeScreen signature
content = content.replace(
    "historyCount: Int = 0,", 
    "historyItems: List<CalculationHistory> = emptyList(),"
)

# Update RecentCalculationsBanner call to pass historyItems
content = content.replace(
    "RecentCalculationsBanner(historyCount, onNavigateToHistory)", 
    "RecentCalculationsBanner(historyItems, onNavigateToHistory)"
)

# Update RecentCalculationsBanner definition
old_banner = """@Composable
fun RecentCalculationsBanner(historyCount: Int, onNavigateToHistory: () -> Unit) {"""
new_banner = """@Composable
fun RecentCalculationsBanner(historyItems: List<CalculationHistory>, onNavigateToHistory: () -> Unit) {"""
content = content.replace(old_banner, new_banner)

# Update the display of historyCount to show the items or at least the count properly.
# But wait, we can just replace the whole RecentCalculationsBanner body to render actual items if list is not empty.
