import os

filepath = "app/src/main/java/com/loanmaster/pro/core/navigation/AppNavigation.kt"
with open(filepath, "r") as f:
    lines = f.readlines()

new_lines = []
for line in lines:
    if "NavigationSuiteScaffold(" in line and "navigationSuiteItems = {" not in line:
        new_lines.append("    NavigationSuiteScaffold(\n")
    elif "navigationSuiteItems = {" in line and "item(" not in line:
        new_lines.append("        navigationSuiteItems = {\n")
    else:
        new_lines.append(line)

with open(filepath, "w") as f:
    f.writelines(new_lines)
