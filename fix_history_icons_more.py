import os

file_path = "app/src/main/java/com/loanmaster/pro/feature/history/HistoryScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

# Add Compare, Eligibility, Currency support to the fallback logic
replacement = """        "Compare" -> {
            CardData(
                calculatorName = "Loan Compare",
                icon = Icons.Rounded.Balance,
                iconColor = Color(0xFF8E24AA),
                dateString = dateString,
                param1Label = "Loan 1",
                param1Value = formatMoney(item.param1 ?: "0"),
                param2Label = "Loan 2",
                param2Value = formatMoney(item.param2 ?: "0"),
                param3Label = "Difference",
                param3Value = formatMoney(item.param3 ?: "0"),
                mainResultLabel = "Better Option",
                mainResultValue = item.param4 ?: "-"
            )
        }
        "Eligibility" -> {
            CardData(
                calculatorName = "Loan Eligibility",
                icon = Icons.Rounded.PersonSearch,
                iconColor = Color(0xFF1E88E5),
                dateString = dateString,
                param1Label = "Income",
                param1Value = formatMoney(item.param1 ?: "0"),
                param2Label = "Obligations",
                param2Value = formatMoney(item.param2 ?: "0"),
                param3Label = "Tenure",
                param3Value = "${item.param3 ?: "0"} Yrs",
                mainResultLabel = "Eligible Loan",
                mainResultValue = formatMoney(item.param4 ?: "0")
            )
        }
        "Currency" -> {
            CardData(
                calculatorName = "Currency Converter",
                icon = Icons.Rounded.CurrencyExchange,
                iconColor = Color(0xFF00ACC1),
                dateString = dateString,
                param1Label = "From",
                param1Value = item.param1 ?: "-",
                param2Label = "To",
                param2Value = item.param2 ?: "-",
                param3Label = "Rate",
                param3Value = item.param3 ?: "-",
                mainResultLabel = "Converted",
                mainResultValue = item.param4 ?: "-"
            )
        }
        else -> {"""

content = content.replace("        else -> {", replacement)

with open(file_path, "w") as f:
    f.write(content)

print("Done")
