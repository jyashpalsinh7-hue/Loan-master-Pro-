import re

with open("app/src/main/java/com/loanmaster/pro/ui/theme/LoanMasterTheme.kt", "r") as f:
    text = f.read()

# AppComponents defaults
text = re.sub(
    r"val iconSmall: Dp = 16\.dp,",
    r"val iconSmall: Dp = 20.dp,",
    text
)
text = re.sub(
    r"val iconMedium: Dp = 20\.dp,",
    r"val iconMedium: Dp = 24.dp,",
    text
)
text = re.sub(
    r"val iconLarge: Dp = 28\.dp,",
    r"val iconLarge: Dp = 32.dp,",
    text
)

# AppComponents compact
compact_components_old = """        AppComponents(
            iconSmall = 16.dp, iconMedium = 20.dp, iconLarge = 28.dp,"""
compact_components_new = """        AppComponents(
            iconSmall = 18.dp, iconMedium = 24.dp, iconLarge = 32.dp,"""
text = text.replace(compact_components_old, compact_components_new)

with open("app/src/main/java/com/loanmaster/pro/ui/theme/LoanMasterTheme.kt", "w") as f:
    f.write(text)
