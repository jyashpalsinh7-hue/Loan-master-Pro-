import os

filepath = 'app/src/main/java/com/loanmaster/pro/feature/emi/EmiScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

target = """    Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
        ) {"""

replacement = """    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {"""

if target in content:
    content = content.replace(target, replacement)
    with open(filepath, 'w') as f:
        f.write(content)
    print("Fixed!")
else:
    print("Not found.")
