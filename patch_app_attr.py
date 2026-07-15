import sys

file_path = "app/src/main/java/com/loanmaster/pro/LoanMasterApp.kt"
with open(file_path, "r") as f:
    content = f.read()

target = """class LoanMasterApp : Application() {"""
replacement = """class LoanMasterApp : Application() {
    override fun getAttributionTag(): String {
        return "AdMob"
    }"""

if target in content:
    content = content.replace(target, replacement)
    with open(file_path, "w") as f:
        f.write(content)
    print("Patched app attr")
else:
    print("Target not found")
