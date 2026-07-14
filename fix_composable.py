import sys

file_path = "app/src/main/java/com/loanmaster/pro/feature/home/HomeScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

target = """                val allCards = listOf(
                    Triple("Loan Compare", "Compare 2-4 loans") { StandardCalculatorCard("Loan Compare", "Compare 2-4 loans", Icons.Rounded.Balance, Color(0xFF8E24AA), badge = "New", onClick = onNavigateToCompare) },
                    Triple("SIP Calculator", "Plan your SIP & grow wealth") { StandardCalculatorCard("SIP Calculator", "Plan your SIP & grow wealth", Icons.AutoMirrored.Rounded.TrendingUp, Color(0xFF43A047), onClick = onNavigateToSip) },
                    Triple("GST Calculator", "Add or remove GST easily") { StandardCalculatorCard("GST Calculator", "Add or remove GST easily", Icons.Rounded.Receipt, Color(0xFFE53935), onClick = onNavigateToGst) },
                    Triple("RD Calculator", "Calculate Recurring Deposit") { StandardCalculatorCard("RD Calculator", "Calculate Recurring Deposit", Icons.Rounded.CalendarToday, Color(0xFFFF9800), onClick = onNavigateToRd) },
                    Triple("Currency Converter", "Live rates & conversion") { StandardCalculatorCard("Currency Converter", "Live rates & conversion", Icons.Rounded.CurrencyExchange, Color(0xFF00ACC1), onClick = onNavigateToCurrency) },
                    Triple("Loan Eligibility", "Check eligibility quickly") { StandardCalculatorCard("Loan Eligibility", "Check eligibility quickly", Icons.Rounded.PersonSearch, Color(0xFF1E88E5), badge = "Premium", onClick = onNavigateToEligibility) },
                    Triple("FD Calculator", "Calculate FD returns") { StandardCalculatorCard("FD Calculator", "Calculate FD returns", Icons.Rounded.Savings, Color(0xFFD81B60), onClick = onNavigateToFd) },
                    Triple("Loan Prepayment", "Check interest saved") { StandardCalculatorCard("Loan Prepayment", "Check interest saved", Icons.Rounded.EditNote, Color(0xFF5E35B1), onClick = onNavigateToPrepayment) },
                    Triple(summaryTitle, summaryDesc) { StandardCalculatorCard(summaryTitle, summaryDesc, Icons.Rounded.Summarize, Color(0xFF1E88E5), onClick = onNavigateToLoanSummary) },
                    Triple("History", "Recent calculations") { StandardCalculatorCard("History", "Recent calculations", Icons.Rounded.History, Color(0xFF607D8B), onClick = onNavigateToHistory) }
                )"""

replacement = """                class CardItem(val title: String, val subtitle: String, val content: @Composable () -> Unit)
                val allCards = listOf(
                    CardItem("Loan Compare", "Compare 2-4 loans") { StandardCalculatorCard("Loan Compare", "Compare 2-4 loans", Icons.Rounded.Balance, Color(0xFF8E24AA), badge = "New", onClick = onNavigateToCompare) },
                    CardItem("SIP Calculator", "Plan your SIP & grow wealth") { StandardCalculatorCard("SIP Calculator", "Plan your SIP & grow wealth", Icons.AutoMirrored.Rounded.TrendingUp, Color(0xFF43A047), onClick = onNavigateToSip) },
                    CardItem("GST Calculator", "Add or remove GST easily") { StandardCalculatorCard("GST Calculator", "Add or remove GST easily", Icons.Rounded.Receipt, Color(0xFFE53935), onClick = onNavigateToGst) },
                    CardItem("RD Calculator", "Calculate Recurring Deposit") { StandardCalculatorCard("RD Calculator", "Calculate Recurring Deposit", Icons.Rounded.CalendarToday, Color(0xFFFF9800), onClick = onNavigateToRd) },
                    CardItem("Currency Converter", "Live rates & conversion") { StandardCalculatorCard("Currency Converter", "Live rates & conversion", Icons.Rounded.CurrencyExchange, Color(0xFF00ACC1), onClick = onNavigateToCurrency) },
                    CardItem("Loan Eligibility", "Check eligibility quickly") { StandardCalculatorCard("Loan Eligibility", "Check eligibility quickly", Icons.Rounded.PersonSearch, Color(0xFF1E88E5), badge = "Premium", onClick = onNavigateToEligibility) },
                    CardItem("FD Calculator", "Calculate FD returns") { StandardCalculatorCard("FD Calculator", "Calculate FD returns", Icons.Rounded.Savings, Color(0xFFD81B60), onClick = onNavigateToFd) },
                    CardItem("Loan Prepayment", "Check interest saved") { StandardCalculatorCard("Loan Prepayment", "Check interest saved", Icons.Rounded.EditNote, Color(0xFF5E35B1), onClick = onNavigateToPrepayment) },
                    CardItem(summaryTitle, summaryDesc) { StandardCalculatorCard(summaryTitle, summaryDesc, Icons.Rounded.Summarize, Color(0xFF1E88E5), onClick = onNavigateToLoanSummary) },
                    CardItem("History", "Recent calculations") { StandardCalculatorCard("History", "Recent calculations", Icons.Rounded.History, Color(0xFF607D8B), onClick = onNavigateToHistory) }
                )"""

if target in content:
    content = content.replace(target, replacement)
    
    target2 = "                filteredCards.forEach { cardInfo ->"
    replacement2 = "                filteredCards.forEach { cardInfo ->"
    content = content.replace("it.first.contains", "it.title.contains")
    content = content.replace("it.second.contains", "it.subtitle.contains")
    content = content.replace("cardInfo.third()", "cardInfo.content()")
    
    with open(file_path, "w") as f:
        f.write(content)
    print("Fixed Composable error")
else:
    print("Target not found")

