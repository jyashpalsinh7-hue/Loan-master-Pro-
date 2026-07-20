import sys

filename = 'app/src/main/java/com/loanmaster/pro/feature/compare/CompareScreen.kt'
with open(filename, 'r') as f:
    content = f.read()

# Remove unused LoanInputState
start = content.find("class LoanInputState(")
if start != -1:
    end = content.find("}", content.find("fun clear()", start)) + 1
    content = content[:start] + content[end:]

with open(filename, 'w') as f:
    f.write(content)
print("Done")
