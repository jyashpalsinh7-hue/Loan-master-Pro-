import sys

file_path = "app/src/main/java/com/loanmaster/pro/LoanMasterApp.kt"
with open(file_path, "r") as f:
    content = f.read()

target = """    override fun getAttributionTag(): String {
        return "default_tag"
    }"""
replacement = """"""

if target in content:
    content = content.replace(target, replacement)
    with open(file_path, "w") as f:
        f.write(content)
    print("Patched app")
else:
    print("Target not found")
