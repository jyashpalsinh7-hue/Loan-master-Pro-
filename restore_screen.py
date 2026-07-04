with open("app/src/main/java/com/loanmaster/pro/EmiCalculatorScreen.kt", "r") as f:
    content = f.read()

content = content.replace("parsedLoanAmountText", "loanAmountText")
content = content.replace("updateInputs(parsedLoanAmount = it)", "updateInputs(loanAmount = it)")
content = content.replace("parsedLoanAmount = parsedLoanAmount,", "loanAmount = parsedLoanAmount,")
content = content.replace("formatMoney(parsedLoanAmount.toDouble())", "formatMoney(parsedLoanAmount)")

with open("app/src/main/java/com/loanmaster/pro/EmiCalculatorScreen.kt", "w") as f:
    f.write(content)
