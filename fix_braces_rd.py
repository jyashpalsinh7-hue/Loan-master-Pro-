import sys

filepath = 'app/src/main/java/com/loanmaster/pro/feature/rd/RdScreen.kt'
with open(filepath, 'r') as f:
    lines = f.readlines()

# We need to remove the last two lines that contain just "}" or similar
# Let's count open/close braces
content = "".join(lines)
open_count = content.count('{')
close_count = content.count('}')

diff = close_count - open_count
print(f"Brace difference: {diff}")

if diff > 0:
    for i in range(len(lines)-1, -1, -1):
        if '}' in lines[i]:
            lines[i] = lines[i].replace('}', '', 1)
            diff -= 1
            if diff == 0:
                break
    with open(filepath, 'w') as f:
        f.writelines(lines)
    print("Fixed extra braces.")
