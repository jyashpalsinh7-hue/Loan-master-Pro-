import re

with open("app/src/main/java/com/loanmaster/pro/SipCalculatorScreen.kt", "r") as f:
    content = f.read()

# Replace initialization from history
history_init_regex = r"    LaunchedEffect\(initialHistory\) \{\n        viewModel\.initializeFromHistory\(initialHistory\)\n        if \(initialHistory != null\) \{\n            onHistoryConsumed\(\)\n        \}\n    \}"
new_history_init = r"""    LaunchedEffect(initialHistory) {
        if (initialHistory != null) {
            viewModel.onEvent(SipEvent.InitializeFromHistory(initialHistory))
            onHistoryConsumed()
        }
    }"""
content = re.sub(history_init_regex, new_history_init, content)

# Replace LaunchedEffect for updating history
update_history_regex = r"            historyViewModel\.insert\(history\) \{ id ->\n                viewModel\.updateHistoryId\(id\)\n            \}"
new_update_history = r"""            historyViewModel.insert(history) { id ->
                viewModel.onEvent(SipEvent.HistoryIdUpdated(id))
            }"""
content = re.sub(update_history_regex, new_update_history, content)

# Replace InputsSection call
inputs_call_regex = r"InputsSection\(\n                amountText, returnRateText, yearsText, stepUpText, \n                \{ viewModel\.updateInputs\(amount = it\) \}, \n                \{ viewModel\.updateInputs\(returnRate = it\) \}, \n                \{ viewModel\.updateInputs\(years = it\) \}, \n                \{ viewModel\.updateInputs\(stepUp = it\) \}, \n                isWide\n            \)"
new_inputs_call = r"""InputsSection(
                uiState = uiState, 
                onEvent = viewModel::onEvent, 
                isWide = isWide
            )"""
content = re.sub(inputs_call_regex, new_inputs_call, content)

# Replace InputsSection signature
inputs_sig_regex = r"@Composable\nprivate fun InputsSection\(\n    amount: String, returnRate: String, years: String, stepUp: String,\n    onAmount: \(String\) -> Unit, onRate: \(String\) -> Unit, onYears: \(String\) -> Unit, onStepUp: \(String\) -> Unit,\n    isWide: Boolean\n\) \{"
new_inputs_sig = r"""@Composable
private fun InputsSection(
    uiState: SipUiState,
    onEvent: (SipEvent) -> Unit,
    isWide: Boolean
) {
    val amount = uiState.amountText
    val returnRate = uiState.returnRateText
    val years = uiState.yearsText
    val stepUp = uiState.stepUpText
    val onAmount: (String) -> Unit = { onEvent(SipEvent.AmountChanged(it)) }
    val onRate: (String) -> Unit = { onEvent(SipEvent.ReturnRateChanged(it)) }
    val onYears: (String) -> Unit = { onEvent(SipEvent.YearsChanged(it)) }
    val onStepUp: (String) -> Unit = { onEvent(SipEvent.StepUpChanged(it)) }
"""
content = re.sub(inputs_sig_regex, new_inputs_sig, content)

with open("app/src/main/java/com/loanmaster/pro/SipCalculatorScreen.kt", "w") as f:
    f.write(content)
