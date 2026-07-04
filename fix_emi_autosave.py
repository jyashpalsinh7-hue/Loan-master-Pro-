import re

with open("app/src/main/java/com/loanmaster/pro/EmiCalculatorScreen.kt", "r") as f:
    content = f.read()

autosave = """
    LaunchedEffect(loanAmountText, interestRateText, tenureInputText, isTenureInMonths, loanType) {
        kotlinx.coroutines.delay(2000)
        if (historyViewModel != null && hasValidInput) {
            val history = CalculationHistory(
                id = currentHistoryId,
                calculatorType = "EMI",
                title = "$loanType - ${formatMoney(parsedLoanAmount)}",
                param1 = loanAmountText,
                param2 = interestRateText,
                param3 = tenureInputText,
                param4 = isTenureInMonths.toString(),
                param5 = loanType,
                result1 = monthlyEmi,
                result2 = totalInterest,
                result3 = totalPayment
            )
            historyViewModel.insert(history) { id ->
                viewModel.updateHistoryId(id)
            }
        }
    }
"""

content = content.replace("LaunchedEffect(initialHistory) {", autosave + "\n    LaunchedEffect(initialHistory) {")

with open("app/src/main/java/com/loanmaster/pro/EmiCalculatorScreen.kt", "w") as f:
    f.write(content)
