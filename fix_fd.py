import os

fd_screen = "app/src/main/java/com/loanmaster/pro/feature/fd/FdScreen.kt"
if os.path.exists(fd_screen):
    with open(fd_screen, 'r') as f:
        content = f.read()
    
    content = content.replace("formatMoneyExact(value)", "com.loanmaster.pro.core.formatter.formatMoney(value)")
    
    with open(fd_screen, 'w') as f:
        f.write(content)
