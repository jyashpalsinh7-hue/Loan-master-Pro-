import re
import os

state_path = "app/src/main/java/com/loanmaster/pro/feature/prepayment/PrepaymentUiState.kt"
vm_path = "app/src/main/java/com/loanmaster/pro/feature/prepayment/PrepaymentViewModel.kt"
screen_path = "app/src/main/java/com/loanmaster/pro/feature/prepayment/PrepaymentScreen.kt"

# Update PrepaymentUiState
with open(state_path, "r") as f:
    state = f.read()

state = state.replace("val hasValidInput: Boolean = false", "val hasValidInput: Boolean = false,\n    val standardSchedule: List<com.loanmaster.pro.domain.calculator.PrepaymentAmortizationRow> = emptyList(),\n    val prepaySchedule: List<com.loanmaster.pro.domain.calculator.PrepaymentAmortizationRow> = emptyList()")
with open(state_path, "w") as f:
    f.write(state)

# Update PrepaymentViewModel
with open(vm_path, "r") as f:
    vm = f.read()

vm = re.sub(r'sealed class PrepaymentEvent \{.*?\}\n', '', vm, flags=re.DOTALL)

new_func = """    fun updateInputs(
        loanAmount: String? = null,
        interestRate: String? = null,
        tenureYears: String? = null,
        prepaymentAmount: String? = null,
        strategy: String? = null,
        monthlyPrepayment: String? = null,
        annualPrepayment: String? = null,
        historyId: Int? = null,
        history: CalculationHistory? = null
    ) {
        if (history != null) {
            updateState { 
                it.copy(
                    loanAmountText = history.param1 ?: "",
                    interestRateText = history.param2 ?: "",
                    tenureYearsText = history.param3 ?: "",
                    prepaymentAmountText = history.param4 ?: "",
                    strategy = history.param5?.takeIf { it.isNotEmpty() } ?: "Tenure",
                    currentHistoryId = history.id
                )
            }
            return
        }
        
        updateState { current ->
            current.copy(
                loanAmountText = loanAmount ?: current.loanAmountText,
                interestRateText = interestRate ?: current.interestRateText,
                tenureYearsText = tenureYears ?: current.tenureYearsText,
                prepaymentAmountText = prepaymentAmount ?: current.prepaymentAmountText,
                strategy = strategy ?: current.strategy,
                monthlyPrepaymentText = monthlyPrepayment ?: current.monthlyPrepaymentText,
                annualPrepaymentText = annualPrepayment ?: current.annualPrepaymentText,
                currentHistoryId = historyId ?: current.currentHistoryId
            )
        }
    }"""
    
vm = re.sub(r'fun onEvent\(event: PrepaymentEvent\) \{.*?\}\n\n', new_func + "\n\n", vm, flags=re.DOTALL)
vm = vm.replace("hasValidInput = result.isValid", "hasValidInput = result.isValid,\n                standardSchedule = result.standardSchedule,\n                prepaySchedule = result.prepaySchedule")

with open(vm_path, "w") as f:
    f.write(vm)

# Update PrepaymentScreen
with open(screen_path, "r") as f:
    screen = f.read()

screen = screen.replace("viewModel.onEvent(PrepaymentEvent.LoanAmountChanged(it))", "viewModel.updateInputs(loanAmount = it)")
screen = screen.replace("viewModel.onEvent(PrepaymentEvent.InterestRateChanged(it))", "viewModel.updateInputs(interestRate = it)")
screen = screen.replace("viewModel.onEvent(PrepaymentEvent.TenureChanged(it))", "viewModel.updateInputs(tenureYears = it)")
screen = screen.replace("viewModel.onEvent(PrepaymentEvent.PrepaymentAmountChanged(it))", "viewModel.updateInputs(prepaymentAmount = it)")
screen = screen.replace("viewModel.onEvent(PrepaymentEvent.StrategyChanged(it))", "viewModel.updateInputs(strategy = it)")
screen = screen.replace("viewModel.onEvent(PrepaymentEvent.MonthlyPrepaymentChanged(it))", "viewModel.updateInputs(monthlyPrepayment = it)")
screen = screen.replace("viewModel.onEvent(PrepaymentEvent.AnnualPrepaymentChanged(it))", "viewModel.updateInputs(annualPrepayment = it)")
screen = screen.replace("viewModel.onEvent(PrepaymentEvent.HistoryIdUpdated(0))", "viewModel.updateInputs(historyId = 0)")
screen = re.sub(r'viewModel\.onEvent\(PrepaymentEvent\.InitializeFromHistory\(history\)\)', 'viewModel.updateInputs(history = history)', screen)

# Replace data class AmortizationRow
screen = re.sub(r'data class AmortizationRow\(.*?\n\)\n', '', screen, flags=re.DOTALL)

# Replace generateStandardSchedule
screen = re.sub(r'fun generateStandardSchedule.*?return schedule\n\}\n', '', screen, flags=re.DOTALL)
screen = re.sub(r'fun generatePrepaymentSchedule.*?return schedule\n\}\n', '', screen, flags=re.DOTALL)

# Update AmortizationBottomSheet args
# Instead of passing primitives, pass standardSchedule and prepaySchedule directly.
screen = re.sub(r'fun AmortizationBottomSheet\(\n.*?isUnlocked', 'fun AmortizationBottomSheet(standardSchedule: List<com.loanmaster.pro.domain.calculator.PrepaymentAmortizationRow>, prepaySchedule: List<com.loanmaster.pro.domain.calculator.PrepaymentAmortizationRow>, isUnlocked', screen, flags=re.DOTALL)

screen = re.sub(r'var isStandard by remember \{ mutableStateOf\(true\) \}.*?val schedule = if \(isStandard\) standardSchedule else prepaySchedule', 'var isStandard by remember { mutableStateOf(true) }\n    val schedule = if (isStandard) standardSchedule else prepaySchedule', screen, flags=re.DOTALL)

# Update calling of AmortizationBottomSheet
screen = re.sub(r'AmortizationBottomSheet\(\n\s*p = p, prePay = prePay, monthlyPrepay = monthlyPrepay, annualPrepay = annualPrepay, r = r, n = n, originalEmi = emi, strategy = strategy,', 'AmortizationBottomSheet(standardSchedule = uiState.standardSchedule, prepaySchedule = uiState.prepaySchedule,', screen, flags=re.DOTALL)

# Fix AmortizationRow usages
screen = screen.replace("List<AmortizationRow>", "List<com.loanmaster.pro.domain.calculator.PrepaymentAmortizationRow>")
screen = screen.replace("row: AmortizationRow", "row: com.loanmaster.pro.domain.calculator.PrepaymentAmortizationRow")

with open(screen_path, "w") as f:
    f.write(screen)
