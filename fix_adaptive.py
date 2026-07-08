import re
with open('app/src/main/java/com/loanmaster/pro/core/responsive/Responsive.kt', 'r') as f:
    content = f.read()

old_code = """    if (columns == 1) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)
        ) {
            content1(Modifier.fillMaxWidth())
            content2(Modifier.fillMaxWidth())
            content3(Modifier.fillMaxWidth())
        }
    } else {"""

new_code = """    if (columns < 3) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.md)
        ) {
            content1(Modifier.fillMaxWidth())
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(LoanMasterTheme.spacing.gridGutter)
            ) {
                content2(Modifier.weight(1f))
                content3(Modifier.weight(1f))
            }
        }
    } else {"""

content = content.replace(old_code, new_code)

with open('app/src/main/java/com/loanmaster/pro/core/responsive/Responsive.kt', 'w') as f:
    f.write(content)
