import os

file_path = "app/src/main/java/com/loanmaster/pro/MainActivity.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace(".addMigrations(", ".fallbackToDestructiveMigration().addMigrations(")

with open(file_path, "w") as f:
    f.write(content)

file_path = "app/src/main/java/com/loanmaster/pro/data/local/room/LoanMasterDatabase.kt"
with open(file_path, "r") as f:
    content = f.read()

content = content.replace("version = 5", "version = 6")

with open(file_path, "w") as f:
    f.write(content)

print("Done")
