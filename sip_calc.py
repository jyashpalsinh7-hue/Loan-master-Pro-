import re
import os

calc_path = "app/src/main/java/com/loanmaster/pro/domain/calculator/SipCalculator.kt"
state_path = "app/src/main/java/com/loanmaster/pro/feature/sip/SipUiState.kt"
vm_path = "app/src/main/java/com/loanmaster/pro/feature/sip/SipViewModel.kt"
screen_path = "app/src/main/java/com/loanmaster/pro/feature/sip/SipScreen.kt"

# Update SipCalculator
with open(calc_path, "r") as f:
    calc = f.read()
calc = calc.replace("val isValid: Boolean = false", "val isValid: Boolean = false,\n    val inflationAdjustedValue: Double = 0.0")
calc = calc.replace("return SipResult(", "val inflationRate = 0.06\n        val inflationAdjustedValue = maturityValue / kotlin.math.pow(1 + inflationRate, years.toDouble())\n        return SipResult(")
calc = calc.replace("isValid = true", "isValid = true,\n            inflationAdjustedValue = inflationAdjustedValue")
with open(calc_path, "w") as f:
    f.write(calc)

# Update SipUiState
with open(state_path, "r") as f:
    state = f.read()
state = state.replace("val hasValidInput: Boolean = false", "val hasValidInput: Boolean = false,\n    val inflationAdjustedValue: Double = 0.0")
with open(state_path, "w") as f:
    f.write(state)

# Update SipViewModel
with open(vm_path, "r") as f:
    vm = f.read()
vm = vm.replace("hasValidInput = result.isValid", "hasValidInput = result.isValid,\n                inflationAdjustedValue = result.inflationAdjustedValue")
with open(vm_path, "w") as f:
    f.write(vm)

# Update SipScreen to use state instead of calculations
with open(screen_path, "r") as f:
    screen = f.read()
screen = re.sub(r'val inflationRate = 0\.06.*?\n\s*val adjustedValue = maturityValue \/ Math\.pow\(1 \+ inflationRate, years\.toDouble\(\)\)\n\s*val valueLost = maturityValue - adjustedValue',
    'val adjustedValue = uiState.inflationAdjustedValue\n    val valueLost = maturityValue - adjustedValue', screen, flags=re.DOTALL)
screen = re.sub(r'val adjustedValue = maturity / Math\.pow\(1 \+ 0\.06, years\.toDouble\(\)\)',
    'val adjustedValue = uiState.inflationAdjustedValue', screen)
    
# We need to pass uiState to InflationAdjustedCard in SipScreen
screen = screen.replace("private fun InflationAdjustedCard(maturityValue: Double, years: Int)", "private fun InflationAdjustedCard(maturityValue: Double, years: Int, uiState: SipUiState)")
screen = screen.replace("InflationAdjustedCard(maturityValue, yearsText.toIntOrNull() ?: 0)", "InflationAdjustedCard(maturityValue, yearsText.toIntOrNull() ?: 0, uiState)")

with open(screen_path, "w") as f:
    f.write(screen)
