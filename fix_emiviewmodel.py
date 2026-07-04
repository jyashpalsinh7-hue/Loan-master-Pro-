import re

with open("app/src/main/java/com/loanmaster/pro/EmiCalculatorViewModel.kt", "r") as f:
    content = f.read()

# Add HistoryRepository to constructor
content = content.replace("class EmiCalculatorViewModel : ViewModel() {", """import androidx.lifecycle.ViewModelProvider

class EmiCalculatorViewModel(
    private val historyRepository: HistoryRepository
) : ViewModel() {""")

# add factory at the bottom
factory = """
class EmiCalculatorViewModelFactory(
    private val historyRepository: HistoryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EmiCalculatorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EmiCalculatorViewModel(historyRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
"""
content += factory

# remove initializeFromHistory and updateHistoryId
content = re.sub(r'\s*fun updateHistoryId\(id: Int\) \{[^\}]+\}', '', content)
content = re.sub(r'\s*fun initializeFromHistory\(history: CalculationHistory\?\) \{[^\}]+\}', '', content)

# add saveCurrentCalculation and loadFromHistory
save_and_load = """
    private val _isSavedSuccessfully = MutableStateFlow(false)
    val isSavedSuccessfully: StateFlow<Boolean> = _isSavedSuccessfully.asStateFlow()

    fun loadFromHistory(history: CalculationHistory) {
        _loanAmountText.value = history.param1 ?: ""
        _interestRateText.value = history.param2 ?: ""
        _tenureInputText.value = history.param3 ?: ""
        _isTenureInMonths.value = history.param4 == "true"
        _loanType.value = history.param5 ?: "Home Loan"
        _currentHistoryId.value = history.id
    }

    fun saveCurrentCalculation() {
        val amount = _parsedLoanAmount.value
        val rate = _parsedInterestRate.value
        val tenure = _parsedTenureYears.value
        
        if (amount <= 0 || rate <= 0 || tenure <= 0) return

        val history = CalculationHistory(
            id = _currentHistoryId.value,
            calculatorType = "EMI",
            title = "${_loanType.value} - ${formatMoney(amount)}",
            param1 = _loanAmountText.value,
            param2 = _interestRateText.value,
            param3 = _tenureInputText.value,
            param4 = _isTenureInMonths.value.toString(),
            param5 = _loanType.value,
            result1 = _monthlyEmi.value,
            result2 = _totalInterest.value,
            result3 = _totalPayment.value
        )
        
        androidx.lifecycle.viewModelScope.launch {
            val id = historyRepository.saveHistory(history)
            if (id > 0) {
                _currentHistoryId.value = id.toInt()
                _isSavedSuccessfully.value = true
                kotlinx.coroutines.delay(2000)
                _isSavedSuccessfully.value = false
            }
        }
    }
"""

# Insert save_and_load before the last closing brace of EmiCalculatorViewModel
# Find the last closing brace of EmiCalculatorViewModel class (which is before the factory we just appended)
# Actually, I can just replace `val opportunities: StateFlow<List<SmartOpportunity>> = _opportunities.asStateFlow()`
content = content.replace("val opportunities: StateFlow<List<SmartOpportunity>> = _opportunities.asStateFlow()", "val opportunities: StateFlow<List<SmartOpportunity>> = _opportunities.asStateFlow()\n" + save_and_load)

with open("app/src/main/java/com/loanmaster/pro/EmiCalculatorViewModel.kt", "w") as f:
    f.write(content)
