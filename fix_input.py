with open("app/src/main/java/com/loanmaster/pro/feature/prepayment/PrepaymentScreen.kt", "r") as f:
    content = f.read()

content = content.replace(
    'PremiumInputField(',
    'PrepaymentInputField('
)

content = content.replace(
    'fun PrepaymentInputField(',
    'private fun PrepaymentInputField('
)

with open("app/src/main/java/com/loanmaster/pro/feature/prepayment/PrepaymentScreen.kt", "w") as f:
    f.write(content)
