import sys

filepath = 'app/src/main/java/com/loanmaster/pro/feature/fd/FdScreen.kt'
with open(filepath, 'r') as f:
    lines = f.readlines()

new_lines = []
for i, line in enumerate(lines):
    if i == 611: # line 612
        continue # skip the extra brace
    new_lines.append(line)

new_lines.append("}\n") # Add brace at the end of the file

with open(filepath, 'w') as f:
    f.writelines(new_lines)
