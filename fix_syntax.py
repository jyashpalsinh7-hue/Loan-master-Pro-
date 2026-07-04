with open("app/src/main/java/com/loanmaster/pro/EmiCalculatorViewModel.kt", "r") as f:
    content = f.read()

# Fix the broken initializeFromHistory remnant
broken_str = """    } ?: "Home Loan"
            _currentHistoryId.value = history.id
            calculateResults()
        }
    }"""
content = content.replace(broken_str, "    }")

# Also need to import viewModelScope and launch, delay
# Let's add them at the top
imports = """import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
"""
content = content.replace("import androidx.lifecycle.ViewModelProvider", "import androidx.lifecycle.ViewModelProvider\n" + imports)

# fix androidx.lifecycle.viewModelScope.launch to viewModelScope.launch
content = content.replace("androidx.lifecycle.viewModelScope.launch", "viewModelScope.launch")
content = content.replace("kotlinx.coroutines.delay", "delay")

with open("app/src/main/java/com/loanmaster/pro/EmiCalculatorViewModel.kt", "w") as f:
    f.write(content)
