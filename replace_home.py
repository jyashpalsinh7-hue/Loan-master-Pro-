import sys
import re

file_path = "app/src/main/java/com/loanmaster/pro/feature/home/HomeScreen.kt"
with open(file_path, "r") as f:
    content = f.read()

pattern = r"(item\(span = \{ androidx\.compose\.foundation\.lazy\.grid\.GridItemSpan\(maxLineSpan\) \}\) \{\s+HeroBanner\(\)\s+\}\s+// Calculators Header\s+item\(span = \{ androidx\.compose\.foundation\.lazy\.grid\.GridItemSpan\(maxLineSpan\) \}\) \{\s+CalculatorsSectionHeader\(\)\s+\}\s+// Calculator Cards.*?\s+item\(span = \{ androidx\.compose\.foundation\.lazy\.grid\.GridItemSpan\(maxLineSpan\) \}\) \{\s+Spacer\(modifier = Modifier\.heightIn\(min = LoanMasterTheme\.spacing\.xl\)\)\s+\})"

# we will just replace from HeroBanner() down to Spacer
# Actually let's just do a string replacement.

target_start = "                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {\n                    HeroBanner()\n                }"
target_end = "                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {\n                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xl))\n                }"

start_idx = content.find(target_start)
end_idx = content.find(target_end) + len(target_end)

if start_idx != -1 and end_idx != -1:
    replacement = """                if (searchQuery.isBlank()) {
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                        HeroBanner()
                    }
                }
                
                // Calculators Header
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                    val headerText = if (searchQuery.isBlank()) "Calculators" else "Search Results"
                    Text(
                        text = headerText,
                        color = TextPrimary,
                        style = LoanMasterTheme.typography.title,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth().padding(top = LoanMasterTheme.spacing.lg, bottom = LoanMasterTheme.spacing.md)
                    )
                }
                
                val summaryTitle = if (activeLoans.isNotEmpty()) "Active Loans (${activeLoans.size})" else "Loan Summary"
                val summaryDesc = if (activeLoans.isNotEmpty()) "Total: ${com.loanmaster.pro.core.formatter.formatMoney(activeLoans.sumOf { it.principalAmount })}" else "View active loans"

                val allCards = listOf(
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
                )

                val filteredCards = allCards.filter {
                    it.first.contains(searchQuery, ignoreCase = true) || it.second.contains(searchQuery, ignoreCase = true)
                }

                if ("EMI Calculator".contains(searchQuery, ignoreCase = true) || "Calculate monthly EMI".contains(searchQuery, ignoreCase = true)) {
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                        EmiCalculatorCard(onNavigateToEmi)
                    }
                }

                filteredCards.forEach { cardInfo ->
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(1) }) {
                        cardInfo.third()
                    }
                }

                if (searchQuery.isBlank()) {
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                        RecentCalculationsBanner(historyItems, onNavigateToHistory, onNavigateToCalculator)
                    }
                    item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                        QuickToolsSection(isQuickToolsExpanded, onToggleExpand = { viewModel.toggleQuickToolsExpanded() })
                    }
                }
                
                item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(maxLineSpan) }) {
                    Spacer(modifier = Modifier.heightIn(min = LoanMasterTheme.spacing.xl))
                }"""
    new_content = content[:start_idx] + replacement + content[end_idx:]
    with open(file_path, "w") as f:
        f.write(new_content)
    print("HomeScreen updated")
else:
    print("Could not find start or end index")
