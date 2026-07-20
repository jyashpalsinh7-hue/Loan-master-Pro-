import sys

filename = 'app/src/main/java/com/loanmaster/pro/feature/history/HistoryScreen.kt'
with open(filename, 'r') as f:
    content = f.read()

target = """            LazyColumn(
                contentPadding = PaddingValues(
                    horizontal = LoanMasterTheme.spacing.screenPadding,
                    vertical = LoanMasterTheme.spacing.md
                ),
                verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md),
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            ) {"""

replacement = """            com.loanmaster.pro.core.responsive.ResponsiveScreenWrapper(
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            ) {
                LazyColumn(
                    contentPadding = PaddingValues(
                        horizontal = LoanMasterTheme.spacing.screenPadding,
                        vertical = LoanMasterTheme.spacing.md
                    ),
                    verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md),
                    modifier = Modifier.fillMaxSize()
                ) {"""

content = content.replace(target, replacement)
content = content.replace("                }\n            }\n        }\n    }\n", "                }\n            }\n        }\n        }\n    }\n")

with open(filename, 'w') as f:
    f.write(content)
print("Done")
