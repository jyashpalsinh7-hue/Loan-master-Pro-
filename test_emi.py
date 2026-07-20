import os

filepath = 'app/src/main/java/com/loanmaster/pro/feature/emi/EmiScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

# Let's see what happens if I remove Scaffold
print("Scaffold found:", "Scaffold" in content)
