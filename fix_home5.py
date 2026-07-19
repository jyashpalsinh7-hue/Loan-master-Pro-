with open("app/src/main/java/com/loanmaster/pro/feature/home/HomeScreen.kt", "r") as f:
    content = f.read()

content = content.replace("fun HeroBanner() {", "fun HeroBanner(onPremiumClick: () -> Unit = {}) {")

# And we need to fix the call to HeroBanner in HomeScreen
content = content.replace("HeroBanner()", "HeroBanner(onPremiumClick = { showUnlockDialog = true })")

with open("app/src/main/java/com/loanmaster/pro/feature/home/HomeScreen.kt", "w") as f:
    f.write(content)
