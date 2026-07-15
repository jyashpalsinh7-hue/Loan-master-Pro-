import sys

file_path = "app/src/main/java/com/loanmaster/pro/feature/splash/SplashScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

# Make radii proportional
content = content.replace("160.dp.toPx()", "(size.width * 0.4f)")
content = content.replace("240.dp.toPx()", "(size.width * 0.6f)")
content = content.replace("330.dp.toPx()", "(size.width * 0.8f)")

content = content.replace("Modifier.size(246.dp)", "Modifier.size(LoanMasterTheme.components.logoSize + 50.dp)")
content = content.replace("Modifier.size(192.dp)", "Modifier.size(LoanMasterTheme.components.logoSize)")

content = content.replace("17.dp.toPx()", "(size.width * 0.08f)")
content = content.replace("10.dp.toPx()", "(size.width * 0.05f)")
content = content.replace("48.dp.toPx()", "(size.width * 0.25f)")

content = content.replace("36.dp.toPx()", "(size.width * 0.18f)")
content = content.replace("54.dp.toPx()", "(size.width * 0.28f)")
content = content.replace("78.dp.toPx()", "(size.width * 0.40f)")
content = content.replace("102.dp.toPx()", "(size.width * 0.52f)")

content = content.replace("12.dp.toPx()", "(size.width * 0.06f)")
content = content.replace("109.dp.toPx()", "(size.width * 0.55f)")
content = content.replace("111.dp.toPx()", "(size.width * 0.56f)")
content = content.replace("30.dp.toPx()", "(size.width * 0.15f)")
content = content.replace("6.dp.toPx()", "(size.width * 0.03f)")
content = content.replace("14.dp.toPx()", "(size.width * 0.07f)")
content = content.replace("26.dp.toPx()", "(size.width * 0.13f)")
content = content.replace("4.dp.toPx()", "(size.width * 0.02f)")

with open(file_path, "w") as f:
    f.write(content)
print("Patched splash screen")
