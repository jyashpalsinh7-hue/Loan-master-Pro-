import re

with open("app/src/main/java/com/loanmaster/pro/EmiCalculatorScreen.kt", "r") as f:
    content = f.read()

content = content.replace("rec.icon", "when(rec.id) { \"best_savings\" -> Icons.Rounded.Savings; \"fast_track\" -> Icons.Rounded.Speed; \"interest_minimizer\" -> Icons.AutoMirrored.Rounded.TrendingDown; \"custom\" -> Icons.Rounded.AutoAwesome; else -> Icons.Rounded.Info }")
content = content.replace("rec.accentColor", "when(rec.id) { \"best_savings\" -> Color(0xFF22C55E); \"fast_track\" -> Color(0xFF3B82F6); \"interest_minimizer\" -> Color(0xFFA855F7); \"custom\" -> Color(0xFFF59E0B); else -> Color.Gray }")

with open("app/src/main/java/com/loanmaster/pro/EmiCalculatorScreen.kt", "w") as f:
    f.write(content)
