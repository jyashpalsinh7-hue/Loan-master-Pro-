with open("app/src/main/java/com/loanmaster/pro/FdCalculatorViewModel.kt", "r") as f:
    content = f.read()

import re

# Add data class
data_class = """data class FdYearBreakdown(
    val year: Int,
    val deposit: Double,
    val interest: Double,
    val maturity: Double
)

class FdCalculatorViewModel : ViewModel() {"""

content = content.replace("class FdCalculatorViewModel : ViewModel() {", data_class)

# Add StateFlow
state_flow = """    private val _wealthGain = MutableStateFlow(0.0)
    val wealthGain: StateFlow<Double> = _wealthGain.asStateFlow()

    private val _yearBreakdown = MutableStateFlow<List<FdYearBreakdown>>(emptyList())
    val yearBreakdown: StateFlow<List<FdYearBreakdown>> = _yearBreakdown.asStateFlow()"""

content = content.replace("    private val _wealthGain = MutableStateFlow(0.0)\n    val wealthGain: StateFlow<Double> = _wealthGain.asStateFlow()", state_flow)

# Update calculation logic
calc_logic = """            _totalReturns.value = returns
            _wealthGain.value = if (p > 0) (returns / p) * 100 else 0.0

            val breakdown = listOf(1.0, 2.0, 3.0, 4.0, 5.0).map { y ->
                val tMat = p * (1 + ratePerPeriod / n).pow(n * y)
                FdYearBreakdown(y.toInt(), p, tMat - p, tMat)
            }
            _yearBreakdown.value = breakdown
        } else {"""

content = content.replace("            _totalReturns.value = returns\n            _wealthGain.value = if (p > 0) (returns / p) * 100 else 0.0\n        } else {", calc_logic)

empty_calc_logic = """            _totalReturns.value = 0.0
            _wealthGain.value = 0.0
            _yearBreakdown.value = emptyList()
        }
    }"""

content = content.replace("            _totalReturns.value = 0.0\n            _wealthGain.value = 0.0\n        }\n    }", empty_calc_logic)

with open("app/src/main/java/com/loanmaster/pro/FdCalculatorViewModel.kt", "w") as f:
    f.write(content)
