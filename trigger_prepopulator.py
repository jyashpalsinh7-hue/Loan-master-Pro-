import os

file_path = "app/src/main/java/com/loanmaster/pro/MainActivity.kt"
with open(file_path, "r") as f:
    content = f.read()

replacement = """                    // Prepopulate database with 5 items per calculator if empty
                    LaunchedEffect(Unit) {
                        DatabasePrepopulator.prepopulateIfEmpty(context, repository)
                    }"""

content = content.replace("// Prepopulate database with 5 items per calculator if empty", replacement)

with open(file_path, "w") as f:
    f.write(content)

print("Done")
