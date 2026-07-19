import sys

files = [
    "app/src/main/java/com/loanmaster/pro/feature/fd/FdUiState.kt",
    "app/src/main/java/com/loanmaster/pro/feature/fd/FdScreen.kt",
    "app/src/main/java/com/loanmaster/pro/feature/fd/FdViewModel.kt",
    "app/src/test/java/com/loanmaster/pro/FdCalculatorViewModelTest.kt"
]

for path in files:
    with open(path, "r") as f:
        content = f.read()
    
    # Simple replacement of totalReturns to totalInterest
    content = content.replace("totalReturns", "totalInterest")
    
    with open(path, "w") as f:
        f.write(content)
