import sys

# 1. Update FdYearBreakdown in CalculatorModels.kt
path1 = "app/src/main/java/com/loanmaster/pro/domain/model/CalculatorModels.kt"
with open(path1, "r") as f:
    content = f.read()
if "val year: Int," in content:
    content = content.replace("val year: Int,", "val year: Double,")
    with open(path1, "w") as f:
        f.write(content)
        
# 2. Update FdScreen.kt to remove toInt() on year
path2 = "app/src/main/java/com/loanmaster/pro/feature/fd/FdScreen.kt"
with open(path2, "r") as f:
    content2 = f.read()
if "val y = bd.year.toDouble()" in content2:
    content2 = content2.replace("val y = bd.year.toDouble()", "val y = bd.year")
if "Text(\"${y.toInt()}\"," in content2:
    # Need to format nicely, e.g., if it's 2.5 display 2.5, if 2.0 display 2.
    content2 = content2.replace("Text(\"${y.toInt()}\",", "val yDisplay = if (y % 1.0 == 0.0) y.toInt().toString() else y.toString()\n                            Text(yDisplay,")
    with open(path2, "w") as f:
        f.write(content2)
