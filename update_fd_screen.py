import re

with open("app/src/main/java/com/loanmaster/pro/FdCalculatorScreen.kt", "r") as f:
    content = f.read()

# Add viewmodel import
content = content.replace("import kotlin.math.pow", "import kotlin.math.pow\nimport androidx.lifecycle.viewmodel.compose.viewModel\nimport androidx.lifecycle.compose.collectAsStateWithLifecycle")

# Replace function signature and add viewmodel
orig_sig = """fun FdCalculatorScreen(
    onNavigateBack: () -> Unit,
    historyViewModel: HistoryViewModel? = null,
    initialHistory: CalculationHistory? = null,
    onHistoryConsumed: () -> Unit = {}
) {"""

new_sig = """fun FdCalculatorScreen(
    onNavigateBack: () -> Unit,
    historyViewModel: HistoryViewModel? = null,
    initialHistory: CalculationHistory? = null,
    onHistoryConsumed: () -> Unit = {},
    viewModel: FdCalculatorViewModel = viewModel()
) {"""

content = content.replace(orig_sig, new_sig)

# Replace the states and LaunchedEffect
state_block_regex = r"    var depositAmountText by rememberSaveable \{ mutableStateOf\(initialHistory\?\.param1 \?\: \"\"\) \}.*?    \}\n"

new_state_block = """    val depositAmountText by viewModel.depositAmountText.collectAsStateWithLifecycle()
    val interestRatePaText by viewModel.interestRatePaText.collectAsStateWithLifecycle()
    val tenureYearsText by viewModel.tenureYearsText.collectAsStateWithLifecycle()
    val compoundingFrequency by viewModel.compoundingFrequency.collectAsStateWithLifecycle()
    val currentHistoryId by viewModel.currentHistoryId.collectAsStateWithLifecycle()

    val parsedDepositAmount by viewModel.parsedDepositAmount.collectAsStateWithLifecycle()
    val parsedInterestRate by viewModel.parsedInterestRate.collectAsStateWithLifecycle()
    val parsedTenureYears by viewModel.parsedTenureYears.collectAsStateWithLifecycle()
    val maturityValue by viewModel.maturityValue.collectAsStateWithLifecycle()
    val totalInvested by viewModel.totalInvested.collectAsStateWithLifecycle()
    val totalReturns by viewModel.totalReturns.collectAsStateWithLifecycle()
    val wealthGain by viewModel.wealthGain.collectAsStateWithLifecycle()
    val hasValidInput by viewModel.hasValidInput.collectAsStateWithLifecycle()

    var showCompoundingDropdown by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(initialHistory) {
        if (initialHistory != null) {
            viewModel.initializeFromHistory(initialHistory)
            onHistoryConsumed()
        }
    }
"""

content = re.sub(state_block_regex, new_state_block, content, flags=re.DOTALL)

# Replace calculation logic with state variables
calc_logic_regex = r"    LaunchedEffect\(depositAmountText, interestRatePaText, tenureYearsText, compoundingFrequency\) \{.*?    val wealthGain = if \(totalInvested > 0\) \(totalReturns \/ totalInvested\) \* 100 else 0\.0\n"

new_calc_logic = """    LaunchedEffect(depositAmountText, interestRatePaText, tenureYearsText, compoundingFrequency) {
        kotlinx.coroutines.delay(2000)
        val isValid = parsedDepositAmount > 0 && parsedInterestRate > 0 && parsedTenureYears > 0
        if (historyViewModel != null && isValid) {
            val history = CalculationHistory(
                id = currentHistoryId,
                calculatorType = "FD",
                title = "₹${formatInr(parsedDepositAmount)} at $interestRatePaText%",
                param1 = depositAmountText,
                param2 = interestRatePaText,
                param3 = tenureYearsText,
                param4 = compoundingFrequency
            )
            historyViewModel.insert(history) { id ->
                viewModel.updateHistoryId(id)
            }
        }
    }

    val formatDec = { value: Double ->
        val s = String.format(Locale.US, "%.2f", value)
        if (s.endsWith(".00")) s.substring(0, s.length - 3) else s
    }
"""

content = re.sub(calc_logic_regex, new_calc_logic, content, flags=re.DOTALL)

# Replace update callbacks
content = content.replace("depositAmountText = it", "viewModel.updateInputs(depositAmount = it)")
content = content.replace("interestRatePaText = it", "viewModel.updateInputs(interestRatePa = it)")
content = content.replace("tenureYearsText = it", "viewModel.updateInputs(tenureYears = it)")
content = content.replace("compoundingFrequency = option", "viewModel.updateInputs(compoundingFreq = option)")

with open("app/src/main/java/com/loanmaster/pro/FdCalculatorScreen.kt", "w") as f:
    f.write(content)
