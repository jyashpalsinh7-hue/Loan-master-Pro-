import sys

filepath = 'app/src/main/java/com/loanmaster/pro/feature/rd/RdScreen.kt'
with open(filepath, 'r') as f:
    lines = f.readlines()

new_lines = []
for i, line in enumerate(lines):
    if i == 680: # index 680 is line 681 (if 0-indexed)
        # Let's insert '    }\n' here (this is after '    )')
        new_lines.append("    }\n")
    if i in [691, 692]: # lines 692, 693
        continue # skip the extra braces
    new_lines.append(line)

with open(filepath, 'w') as f:
    f.writelines(new_lines)
