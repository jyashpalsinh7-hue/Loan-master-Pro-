with open("app/src/main/java/com/loanmaster/pro/RecommendationBottomSheet.kt", "r") as f:
    content = f.read()

content = content.replace("rec.icon", "when(rec.id) { \"best_savings\" -> Icons.Rounded.Savings; \"fast_track\" -> Icons.Rounded.Speed; \"interest_minimizer\" -> Icons.AutoMirrored.Rounded.TrendingDown; \"custom\" -> Icons.Rounded.AutoAwesome; else -> Icons.Rounded.Info }")
content = content.replace("rec.accentColor", "when(rec.id) { \"best_savings\" -> Color(0xFF22C55E); \"fast_track\" -> Color(0xFF3B82F6); \"interest_minimizer\" -> Color(0xFFA855F7); \"custom\" -> Color(0xFFF59E0B); else -> Color.Gray }")
content = content.replace("@OptIn(ExperimentalMaterial3Api::class)\n@Composable", "@Composable")
content = content.replace("@Composable\nfun RecommendationBottomSheet(", "@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)\n@Composable\nfun RecommendationBottomSheet(")

with open("app/src/main/java/com/loanmaster/pro/RecommendationBottomSheet.kt", "w") as f:
    f.write(content)
