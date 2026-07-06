import re
import os

vm_path = "app/src/main/java/com/loanmaster/pro/feature/prepayment/PrepaymentViewModel.kt"
screen_path = "app/src/main/java/com/loanmaster/pro/feature/prepayment/PrepaymentScreen.kt"
calc_path = "app/src/main/java/com/loanmaster/pro/domain/calculator/PrepaymentCalculator.kt"
state_path = "app/src/main/java/com/loanmaster/pro/feature/prepayment/PrepaymentUiState.kt"

# 1. Add PrepaymentAmortizationRow to Calculator
with open(calc_path, "r") as f:
    calc = f.read()

if "data class PrepaymentAmortizationRow" not in calc:
    calc = calc.replace("data class PrepaymentResult(", """data class PrepaymentAmortizationRow(
    val month: Int,
    val payment: Double,
    val principal: Double,
    val interest: Double,
    val balance: Double,
    val isPrepayment: Boolean = false,
    val label: String = ""
)

data class PrepaymentResult(""")
    
    calc = calc.replace("val isValid: Boolean = false", "val isValid: Boolean = false,\n    val standardSchedule: List<PrepaymentAmortizationRow> = emptyList(),\n    val prepaySchedule: List<PrepaymentAmortizationRow> = emptyList()")
    
    # We will add logic to calculate schedules in the calculator instead of Screen.
    # But it might be too complex to rewrite in script. 
    # Let's just write a new PrepaymentCalculator.kt
    pass

