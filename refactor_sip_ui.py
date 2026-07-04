import re

with open("app/src/main/java/com/loanmaster/pro/SipCalculatorScreen.kt", "r") as f:
    content = f.read()

# Add imports
imports_regex = r"(import androidx.compose.runtime.\*)"
new_imports = r"\1\nimport androidx.lifecycle.viewmodel.compose.viewModel\nimport androidx.lifecycle.compose.collectAsStateWithLifecycle"
content = re.sub(imports_regex, new_imports, content)

# Change function signature
sig_regex = r"(fun SipCalculatorScreen\(\n    onNavigateBack: \(\) -> Unit,\n    historyViewModel: HistoryViewModel\? = null,\n    initialHistory: CalculationHistory\? = null,\n    onHistoryConsumed: \(\) -> Unit = \{\}\n)\) \{"

new_sig = r"\1,\n    viewModel: SipCalculatorViewModel = viewModel()\n) {"
content = re.sub(sig_regex, new_sig, content)

# Replace the state logic inside SipCalculatorScreen
state_regex = r"    // RESPONSIVE: use rememberSaveable\n    var amountText by rememberSaveable \{ mutableStateOf\(initialHistory\?\.param1 \?: \"\"\) \}\n    var returnRateText by rememberSaveable \{ mutableStateOf\(initialHistory\?\.param2 \?: \"\"\) \}\n    var yearsText by rememberSaveable \{ mutableStateOf\(initialHistory\?\.param3 \?: \"\"\) \}\n    var stepUpText by rememberSaveable \{ mutableStateOf\(initialHistory\?\.param4 \?: \"\"\) \}\n        var currentHistoryId by rememberSaveable \{ mutableStateOf\(initialHistory\?\.id \?: 0\) \}\n    LaunchedEffect\(initialHistory\) \{\n        if \(initialHistory != null\) \{\n            onHistoryConsumed\(\)\n        \}\n    \}\n        LaunchedEffect\(amountText, returnRateText, yearsText, stepUpText\) \{\n        kotlinx\.coroutines\.delay\(2000\)\n        val amount = amountText\.toDoubleOrNull\(\) \?: 0\.0\n        val returnRate = returnRateText\.toDoubleOrNull\(\) \?: 0\.0\n        val years = yearsText\.toIntOrNull\(\) \?: 0\n        if \(historyViewModel != null && amount > 0 && returnRate > 0 && years > 0\) \{\n            val history = CalculationHistory\(\n                id = currentHistoryId,\n                calculatorType = \"SIP\",\n                title = \"₹\$amountText for \$yearsText Yrs at \$returnRateText%\",\n                param1 = amountText,\n                param2 = returnRateText,\n                param3 = yearsText,\n                param4 = stepUpText\n            \)\n            historyViewModel\.insert\(history\) \{ id ->\n                currentHistoryId = id\n            \}\n        \}\n    \}\n    val amount = amountText\.toDoubleOrNull\(\) \?: 0\.0\n    val returnRate = returnRateText\.toDoubleOrNull\(\) \?: 0\.0\n    val years = yearsText\.toIntOrNull\(\) \?: 0\n    val stepUpRate = stepUpText\.toDoubleOrNull\(\) \?: 0\.0\n    // Engine Math\n    var totalInvested = 0\.0\n    var maturityValue = 0\.0\n    var currentMonthlySip = amount\n    val monthlyReturnRate = \(returnRate / 100\.0\) / 12\.0\n    val totalMonths = years \* 12\n    val stepUpFraction = stepUpRate / 100\.0        val yearlyDataList = mutableListOf<YearlyData>\(\)\n    var investedThisYear = 0\.0\n    for \(m in 1\.\.totalMonths\) \{\n        totalInvested \+= currentMonthlySip\n        investedThisYear \+= currentMonthlySip\n        maturityValue = \(maturityValue \+ currentMonthlySip\) \* \(1 \+ monthlyReturnRate\)\n        if \(m % 12 == 0\) \{\n            val year = m / 12\n            yearlyDataList\.add\(\n                YearlyData\(\n                    year = year,\n                    investedForYear = investedThisYear,\n                    totalInvested = totalInvested,\n                    returns = maturityValue - totalInvested,\n                    maturity = maturityValue\n                \)\n            \)\n            investedThisYear = 0\.0\n            currentMonthlySip \+= currentMonthlySip \* stepUpFraction\n        \}\n    \}\n    val totalGain = maturityValue - totalInvested"

new_state = """    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    val amountText = uiState.amountText
    val returnRateText = uiState.returnRateText
    val yearsText = uiState.yearsText
    val stepUpText = uiState.stepUpText
    val currentHistoryId = uiState.currentHistoryId
    
    val totalInvested = uiState.totalInvested
    val totalGain = uiState.totalGain
    val maturityValue = uiState.maturityValue
    val yearlyDataList = uiState.yearlyDataList
    val hasValidInput = uiState.hasValidInput
    
    val returnRate = returnRateText.toDoubleOrNull() ?: 0.0
    val years = yearsText.toIntOrNull() ?: 0

    LaunchedEffect(initialHistory) {
        viewModel.initializeFromHistory(initialHistory)
        if (initialHistory != null) {
            onHistoryConsumed()
        }
    }
    
    LaunchedEffect(amountText, returnRateText, yearsText, stepUpText) {
        kotlinx.coroutines.delay(2000)
        if (historyViewModel != null && hasValidInput) {
            val history = CalculationHistory(
                id = currentHistoryId,
                calculatorType = "SIP",
                title = "₹$amountText for $yearsText Yrs at $returnRateText%",
                param1 = amountText,
                param2 = returnRateText,
                param3 = yearsText,
                param4 = stepUpText
            )
            historyViewModel.insert(history) { id ->
                viewModel.updateHistoryId(id)
            }
        }
    }"""
content = re.sub(state_regex, new_state, content, flags=re.DOTALL)

# Update the InputsSection call to use viewModel.updateInputs
inputs_call_regex = r"InputsSection\(amountText, returnRateText, yearsText, stepUpText, \{ amountText = it \}, \{ returnRateText = it \}, \{ yearsText = it \}, \{ stepUpText = it \}, isWide\)"
new_inputs_call = r"""InputsSection(
                amountText, returnRateText, yearsText, stepUpText, 
                { viewModel.updateInputs(amount = it) }, 
                { viewModel.updateInputs(returnRate = it) }, 
                { viewModel.updateInputs(years = it) }, 
                { viewModel.updateInputs(stepUp = it) }, 
                isWide
            )"""
content = re.sub(inputs_call_regex, new_inputs_call, content)

with open("app/src/main/java/com/loanmaster/pro/SipCalculatorScreen.kt", "w") as f:
    f.write(content)
