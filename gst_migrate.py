import re
import os

vm_path = "app/src/main/java/com/loanmaster/pro/feature/gst/GstViewModel.kt"
screen_path = "app/src/main/java/com/loanmaster/pro/feature/gst/GstScreen.kt"

if os.path.exists(vm_path):
    with open(vm_path, "r") as f:
        content = f.read()
    
    content = re.sub(r'sealed class GstEvent \{.*?\}\n', '', content, flags=re.DOTALL)
    
    new_func = """    fun updateInputs(
        amount: String? = null,
        rate: String? = null,
        type: String? = null,
        historyId: Int? = null,
        history: CalculationHistory? = null
    ) {
        if (history != null) {
            updateState { 
                it.copy(
                    amountText = history.param1 ?: "",
                    gstRate = history.param2 ?: "",
                    gstType = history.param3 ?: "Exclusive",
                    currentHistoryId = history.id
                )
            }
            return
        }
        
        updateState { current ->
            current.copy(
                amountText = amount ?: current.amountText,
                gstRate = rate ?: current.gstRate,
                gstType = type ?: current.gstType,
                currentHistoryId = historyId ?: current.currentHistoryId
            )
        }
    }"""
    
    content = re.sub(r'fun onEvent\(event: GstEvent\) \{.*?\}\n\n', new_func + "\n\n", content, flags=re.DOTALL)
    with open(vm_path, "w") as f:
        f.write(content)

if os.path.exists(screen_path):
    with open(screen_path, "r") as f:
        content = f.read()
    
    content = content.replace("viewModel.onEvent(GstEvent.AmountChanged(it))", "viewModel.updateInputs(amount = it)")
    content = content.replace("viewModel.onEvent(GstEvent.RateChanged(rate))", "viewModel.updateInputs(rate = rate)")
    content = content.replace("viewModel.onEvent(GstEvent.RateChanged(it))", "viewModel.updateInputs(rate = it)")
    content = content.replace("viewModel.onEvent(GstEvent.TypeChanged(\"Exclusive\"))", "viewModel.updateInputs(type = \"Exclusive\")")
    content = content.replace("viewModel.onEvent(GstEvent.TypeChanged(\"Inclusive\"))", "viewModel.updateInputs(type = \"Inclusive\")")
    content = content.replace("viewModel.onEvent(GstEvent.HistoryIdUpdated(0))", "viewModel.updateInputs(historyId = 0)")
    content = re.sub(r'viewModel\.onEvent\(GstEvent\.InitializeFromHistory\(history\)\)', 'viewModel.updateInputs(history = history)', content)
    
    with open(screen_path, "w") as f:
        f.write(content)

