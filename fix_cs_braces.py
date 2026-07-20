import sys

filepath = 'app/src/main/java/com/loanmaster/pro/feature/currency/CurrencyScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

total_open = content.count('{')
total_close = content.count('}')

diff = total_close - total_open
if diff > 0:
    lines = content.split('\n')
    for i in range(len(lines)-1, -1, -1):
        if '}' in lines[i]:
            lines[i] = lines[i].replace('}', '', 1)
            diff -= 1
            if diff == 0:
                break
    content = '\n'.join(lines)
    with open(filepath, 'w') as f:
        f.write(content)
elif diff < 0:
    with open(filepath, 'a') as f:
        for _ in range(-diff):
            f.write('\n}')
