import sys

filepath = 'app/src/main/java/com/loanmaster/pro/feature/fd/FdScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

total_open = content.count('{')
total_close = content.count('}')

print(f"Open: {total_open}, Close: {total_close}")

if total_close > total_open:
    diff = total_close - total_open
    print(f"Removing {diff} extra braces from {filepath}")
    
    lines = content.split('\n')
    for i in range(len(lines)-1, -1, -1):
        if '}' in lines[i]:
            lines[i] = lines[i].replace('}', '', 1)
            diff -= 1
            if diff == 0:
                break
    
    with open(filepath, 'w') as f:
        f.write('\n'.join(lines))
    print("Fixed extra braces.")
elif total_open > total_close:
    diff = total_open - total_close
    print(f"Adding {diff} braces.")
    with open(filepath, 'a') as f:
        for _ in range(diff):
            f.write('\n}')

