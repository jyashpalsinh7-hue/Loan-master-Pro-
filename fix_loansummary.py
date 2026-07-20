import os

filepath = 'app/src/main/java/com/loanmaster/pro/feature/loansummary/LoanSummaryScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

target = """    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = bgDark,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddLoanDialog = true },
                containerColor = accentYellow,
                contentColor = bgDark,
                shape = CircleShape
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "Add")
            }
        }
        ) { padding ->
        com.loanmaster.pro.core.responsive.ResponsiveScreenWrapper(modifier = Modifier.padding(padding)) {"""

replacement = """    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgDark)
    ) {
        com.loanmaster.pro.core.responsive.ResponsiveScreenWrapper(modifier = Modifier.fillMaxSize()) {"""

if target in content:
    content = content.replace(target, replacement)
    
    # We need to add the FAB inside the Box manually now
    # We will do this at the end of the file or just inside the Box if possible. Let's just do it manually.
    print("Target found, replacing...")
    with open(filepath, 'w') as f:
        f.write(content)
else:
    print("Target not found.")

