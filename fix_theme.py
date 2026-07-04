import re

with open("app/src/main/java/com/loanmaster/pro/ui/theme/LoanMasterTheme.kt", "r") as f:
    text = f.read()

# Replace AppComponents defaults
text = re.sub(
    r"val buttonHeight: Dp = 56\.dp,",
    r"val buttonHeight: Dp = 48.dp,",
    text
)
text = re.sub(
    r"val iconSmall: Dp = 20\.dp,",
    r"val iconSmall: Dp = 16.dp,",
    text
)
text = re.sub(
    r"val iconMedium: Dp = 24\.dp,",
    r"val iconMedium: Dp = 20.dp,",
    text
)
text = re.sub(
    r"val iconLarge: Dp = 32\.dp,",
    r"val iconLarge: Dp = 28.dp,",
    text
)
text = re.sub(
    r"val topAppBarHeight: Dp = 64\.dp,",
    r"val topAppBarHeight: Dp = 56.dp,",
    text
)
text = re.sub(
    r"val bottomNavHeight: Dp = 80\.dp,",
    r"val bottomNavHeight: Dp = 72.dp,",
    text
)
text = re.sub(
    r"val calculatorCardHeight: Dp = 120\.dp,",
    r"val calculatorCardHeight: Dp = 100.dp,",
    text
)
text = re.sub(
    r"val featuredCardHeight: Dp = 140\.dp",
    r"val featuredCardHeight: Dp = 120.dp",
    text
)

# AppTypographyTokens defaults
text = re.sub(
    r"val display: TextStyle = TextStyle\(fontSize = 32\.sp, fontWeight = FontWeight\.Bold, lineHeight = 40\.sp\),",
    r"val display: TextStyle = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, lineHeight = 36.sp),",
    text
)
text = re.sub(
    r"val title: TextStyle = TextStyle\(fontSize = 20\.sp, fontWeight = FontWeight\.SemiBold, lineHeight = 28\.sp\),",
    r"val title: TextStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold, lineHeight = 24.sp),",
    text
)
text = re.sub(
    r"val body: TextStyle = TextStyle\(fontSize = 16\.sp, fontWeight = FontWeight\.Normal, lineHeight = 24\.sp\),",
    r"val body: TextStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, lineHeight = 20.sp),",
    text
)
text = re.sub(
    r"val label: TextStyle = TextStyle\(fontSize = 12\.sp, fontWeight = FontWeight\.Medium, lineHeight = 16\.sp\)",
    r"val label: TextStyle = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium, lineHeight = 14.sp)",
    text
)

# Now in the compact section, modify AppComponents
compact_components_old = """        AppComponents(
            iconSmall = 18.dp, iconMedium = 24.dp, iconLarge = 32.dp,
            calculatorCardHeight = 120.dp, featuredCardHeight = 140.dp
        )"""
compact_components_new = """        AppComponents(
            iconSmall = 16.dp, iconMedium = 20.dp, iconLarge = 28.dp,
            calculatorCardHeight = 100.dp, featuredCardHeight = 120.dp
        )"""
text = text.replace(compact_components_old, compact_components_new)

# Modify compact typography
compact_typography_old = """        AppTypographyTokens(
            display = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold, lineHeight = 40.sp),
            title = TextStyle(fontSize = 19.sp, fontWeight = FontWeight.SemiBold, lineHeight = 26.sp),
            body = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal, lineHeight = 22.sp),
            label = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium, lineHeight = 16.sp)
        )"""
compact_typography_new = """        AppTypographyTokens(
            display = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, lineHeight = 36.sp),
            title = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.SemiBold, lineHeight = 24.sp),
            body = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, lineHeight = 20.sp),
            label = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium, lineHeight = 14.sp)
        )"""
text = text.replace(compact_typography_old, compact_typography_new)

with open("app/src/main/java/com/loanmaster/pro/ui/theme/LoanMasterTheme.kt", "w") as f:
    f.write(text)
