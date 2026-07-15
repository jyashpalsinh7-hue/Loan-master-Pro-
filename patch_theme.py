import sys

file_path = "app/src/main/java/com/loanmaster/pro/core/theme/LoanMasterTheme.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace("val featuredCardHeight: Dp = 120.dp", 
"val featuredCardHeight: Dp = 120.dp,\n    val bannerHeight: Dp = 140.dp,\n    val heroHeight: Dp = 200.dp,\n    val chartHeight: Dp = 220.dp,\n    val logoSize: Dp = 120.dp,\n    val cardPadding: Dp = 16.dp")

content = content.replace("calculatorCardHeight = 150.dp, featuredCardHeight = 180.dp",
"calculatorCardHeight = 150.dp, featuredCardHeight = 180.dp,\n            bannerHeight = 200.dp, heroHeight = 280.dp, chartHeight = 320.dp, logoSize = 180.dp, cardPadding = 24.dp")

content = content.replace("calculatorCardHeight = 135.dp, featuredCardHeight = 160.dp",
"calculatorCardHeight = 135.dp, featuredCardHeight = 160.dp,\n            bannerHeight = 160.dp, heroHeight = 240.dp, chartHeight = 280.dp, logoSize = 150.dp, cardPadding = 20.dp")

content = content.replace("calculatorCardHeight = 100.dp, featuredCardHeight = 120.dp",
"calculatorCardHeight = 100.dp, featuredCardHeight = 120.dp,\n            bannerHeight = 140.dp, heroHeight = 200.dp, chartHeight = 220.dp, logoSize = 120.dp, cardPadding = 16.dp")

content = content.replace("val calculatorColumns: Int = 1,", "val calculatorColumns: Int = 2,")
content = content.replace("calculatorColumns = 2", "calculatorColumns = 3", 1) # First occurrence is medium, oh wait, second one is compact.
# Let's replace precisely:

target_medium_grids = """    } else if (isMedium) {
        AppGrids(
            calculatorColumns = 2,
            scheduleColumns = 1
        )
    } else {"""
rep_medium_grids = """    } else if (isMedium) {
        AppGrids(
            calculatorColumns = 3,
            scheduleColumns = 1
        )
    } else {"""

content = content.replace(target_medium_grids, rep_medium_grids)

with open(file_path, "w") as f:
    f.write(content)
print("Patched theme")
