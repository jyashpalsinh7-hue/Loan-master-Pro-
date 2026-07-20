import sys

def balance_braces(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    open_count = 0
    # Instead of deleting in the middle which might mess up things,
    # let's just count total { and }.
    
    # We will remove excess } from the end of the file or append { at the beginning if somehow that's better (but it's } at the end)
    total_open = content.count('{')
    total_close = content.count('}')
    
    if total_close > total_open:
        # We have too many }
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
    elif total_open > total_close:
        # We need more }
        diff = total_open - total_close
        print(f"Adding {diff} extra braces to {filepath}")
        with open(filepath, 'a') as f:
            for _ in range(diff):
                f.write('\n}')

balance_braces('app/src/main/java/com/loanmaster/pro/feature/rd/RdScreen.kt')
