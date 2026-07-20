with open("app/src/main/java/com/loanmaster/pro/feature/prepayment/PrepaymentScreen.kt", "r") as f:
    content = f.read()

content = content.replace(
    '    }\n\n@Composable\nfun PrepaymentHeroCard',
    '    }\n}\n\n@Composable\nfun PrepaymentHeroCard'
)

with open("app/src/main/java/com/loanmaster/pro/feature/prepayment/PrepaymentScreen.kt", "w") as f:
    f.write(content)
