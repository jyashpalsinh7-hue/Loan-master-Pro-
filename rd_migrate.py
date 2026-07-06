import re
import os

vm_path = "app/src/main/java/com/loanmaster/pro/feature/rd/RdViewModel.kt"
screen_path = "app/src/main/java/com/loanmaster/pro/feature/rd/RdScreen.kt"

if os.path.exists(vm_path):
    with open(vm_path, "r") as f:
        content = f.read()
    
    # Remove sealed class
    content = re.sub(r'sealed class RdEvent \{.*?\}\n', '', content, flags=re.DOTALL)
    
    new_func = """    fun updateInputs(
        tab: Int? = null,
        monthlyDeposit: String? = null,
        interestRate: String? = null,
        tenureYears: String? = null,
        frequency: String? = null,
        targetAmount: String? = null,
        historyId: Int? = null,
        history: CalculationHistory? = null
    ) {
        if (history != null) {
            updateState { 
                it.copy(
                    selectedTab = if (history.param5 == "Target") 1 else 0,
                    monthlyDepositText = history.param1 ?: "",
                    interestRatePaText = history.param2 ?: "",
                    tenureYearsText = history.param3 ?: "",
                    compoundingFrequency = history.param4 ?: "Quarterly",
                    targetAmountText = if (history.param5 == "Target") history.param1 ?: "" else "",
                    currentHistoryId = history.id
                )
            }
            return
        }
        
        updateState { current ->
            current.copy(
                selectedTab = tab ?: current.selectedTab,
                monthlyDepositText = monthlyDeposit ?: current.monthlyDepositText,
                interestRatePaText = interestRate ?: current.interestRatePaText,
                tenureYearsText = tenureYears ?: current.tenureYearsText,
                compoundingFrequency = frequency ?: current.compoundingFrequency,
                targetAmountText = targetAmount ?: current.targetAmountText,
                currentHistoryId = historyId ?: current.currentHistoryId
            )
        }
    }"""
    
    content = re.sub(r'fun onEvent\(event: RdEvent\) \{.*?\}\n\n', new_func + "\n\n", content, flags=re.DOTALL)
    with open(vm_path, "w") as f:
        f.write(content)

if os.path.exists(screen_path):
    with open(screen_path, "r") as f:
        content = f.read()
    
    content = content.replace("viewModel.onEvent(RdEvent.TabSelected(0))", "viewModel.updateInputs(tab = 0)")
    content = content.replace("viewModel.onEvent(RdEvent.TabSelected(1))", "viewModel.updateInputs(tab = 1)")
    content = content.replace("viewModel.onEvent(RdEvent.MonthlyDepositChanged(it))", "viewModel.updateInputs(monthlyDeposit = it)")
    content = content.replace("viewModel.onEvent(RdEvent.InterestRateChanged(it))", "viewModel.updateInputs(interestRate = it)")
    content = content.replace("viewModel.onEvent(RdEvent.TenureChanged(it))", "viewModel.updateInputs(tenureYears = it)")
    content = content.replace("viewModel.onEvent(RdEvent.CompoundingFrequencyChanged(it))", "viewModel.updateInputs(frequency = it)")
    content = content.replace("viewModel.onEvent(RdEvent.TargetAmountChanged(it))", "viewModel.updateInputs(targetAmount = it)")
    content = content.replace("viewModel.onEvent(RdEvent.HistoryIdUpdated(0))", "viewModel.updateInputs(historyId = 0)")
    content = re.sub(r'viewModel\.onEvent\(RdEvent\.InitializeFromHistory\(history\)\)', 'viewModel.updateInputs(history = history)', content)
    
    with open(screen_path, "w") as f:
        f.write(content)

