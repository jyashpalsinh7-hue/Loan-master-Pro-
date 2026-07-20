import sys

path = "app/src/main/java/com/loanmaster/pro/domain/calculator/FdCalculator.kt"
with open(path, "r") as f:
    content = f.read()

content = content.replace("val displayYear = minOf(y.toDouble(), t)", "val displayYear = minOf(y.toDouble(), t!!)")
content = content.replace("if (y > t && y - 1 < t) t else y.toDouble()", "if (y > t!! && y - 1 < t!!) t!! else y.toDouble()")

with open(path, "w") as f:
    f.write(content)
