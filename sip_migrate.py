import re
import os

vm_path = "app/src/main/java/com/loanmaster/pro/feature/sip/SipViewModel.kt"
screen_path = "app/src/main/java/com/loanmaster/pro/feature/sip/SipScreen.kt"

if os.path.exists(vm_path):
    with open(vm_path, "r") as f:
        content = f.read()
    
    # Remove sealed class SipEvent and its data classes
    content = re.sub(r'sealed class SipEvent \{.*?\}\n', '', content, flags=re.DOTALL)
    
    # Replace onEvent
    new_func = """    fun updateInputs(
        amount: String? = null,
        rate: String? = null,
        years: String? = null,
        stepUp: String? = null,
        historyId: Int? = null,
        history: CalculationHistory? = null
    ) {
        if (history != null) {
            updateState { 
                it.copy(
                    amountText = history.param1 ?: "",
                    returnRateText = history.param2 ?: "",
                    yearsText = history.param3 ?: "",
                    stepUpText = history.param4 ?: "",
                    currentHistoryId = history.id
                )
            }
            return
        }
        
        updateState { current ->
            current.copy(
                amountText = amount ?: current.amountText,
                returnRateText = rate ?: current.returnRateText,
                yearsText = years ?: current.yearsText,
                stepUpText = stepUp ?: current.stepUpText,
                currentHistoryId = historyId ?: current.currentHistoryId
            )
        }
    }"""
    
    # Remove old onEvent
    content = re.sub(r'fun onEvent\(event: SipEvent\) \{.*?\}\n\n', new_func + "\n\n", content, flags=re.DOTALL)
    
    with open(vm_path, "w") as f:
        f.write(content)

if os.path.exists(screen_path):
    with open(screen_path, "r") as f:
        content = f.read()
    
    content = content.replace("viewModel.onEvent(SipEvent.AmountChanged(it))", "viewModel.updateInputs(amount = it)")
    content = content.replace("viewModel.onEvent(SipEvent.ReturnRateChanged(it))", "viewModel.updateInputs(rate = it)")
    content = content.replace("viewModel.onEvent(SipEvent.YearsChanged(it))", "viewModel.updateInputs(years = it)")
    content = content.replace("viewModel.onEvent(SipEvent.StepUpChanged(it))", "viewModel.updateInputs(stepUp = it)")
    content = content.replace("viewModel.onEvent(SipEvent.HistoryIdUpdated(0))", "viewModel.updateInputs(historyId = 0)")
    
    # In LaunchedEffect
    content = re.sub(r'viewModel\.onEvent\(SipEvent\.InitializeFromHistory\(history\)\)', 'viewModel.updateInputs(history = history)', content)
    
    # Move Inflation adjusted calculation
    # We will just pass pre-calculated value from UiState instead
    
    with open(screen_path, "w") as f:
        f.write(content)
