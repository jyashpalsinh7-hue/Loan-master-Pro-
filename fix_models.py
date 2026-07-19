import sys

path1 = "app/src/main/java/com/loanmaster/pro/domain/model/CalculatorModels.kt"
with open(path1, "r") as f:
    content = f.read()

# Only FdYearBreakdown should be Double
# Revert all 'val year: Double,' back to 'val year: Int,'
content = content.replace("val year: Double,", "val year: Int,")

# Then selectively change FdYearBreakdown
target = "data class FdYearBreakdown(\n    val year: Int,"
replacement = "data class FdYearBreakdown(\n    val year: Double,"
content = content.replace(target, replacement)

with open(path1, "w") as f:
    f.write(content)
