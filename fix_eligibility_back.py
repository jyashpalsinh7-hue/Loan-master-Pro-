import re

with open('app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt', 'r') as f:
    content = f.read()

content = content.replace('fun LoanEligibilityScreen(viewModel: LoanEligibilityViewModel = viewModel()) {', 'fun LoanEligibilityScreen(onNavigateBack: () -> Unit = {}, viewModel: LoanEligibilityViewModel = viewModel()) {')
content = content.replace('.clickable { onBackClick() }', '.clickable { onNavigateBack() }')

with open('app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/loanmaster/pro/core/navigation/AppNavigation.kt', 'r') as f:
    content = f.read()

content = content.replace('composable("eligibility") { LoanEligibilityScreen() }', 'composable("eligibility") { LoanEligibilityScreen(onNavigateBack = { navController.popBackStack() }) }')

with open('app/src/main/java/com/loanmaster/pro/core/navigation/AppNavigation.kt', 'w') as f:
    f.write(content)
