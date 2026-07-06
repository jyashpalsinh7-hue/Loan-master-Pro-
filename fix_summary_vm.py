import re

path = "app/src/main/java/com/loanmaster/pro/feature/loansummary/LoanSummaryViewModel.kt"
with open(path, "r") as f:
    content = f.read()

new_impl = """class LoanSummaryViewModel(private val repository: ActiveLoanRepository) : ViewModel() {
    val uiState: StateFlow<LoanSummaryUiState> = repository.getAllActiveLoans()
        .map { LoanSummaryUiState(activeLoans = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LoanSummaryUiState()
        )

    fun addLoan(loan: ActiveLoan) {
        viewModelScope.launch {
            repository.insertActiveLoan(loan)
        }
    }

    fun deleteLoan(loan: ActiveLoan) {
        viewModelScope.launch {
            repository.deleteActiveLoan(loan)
        }
    }
}
"""

content = re.sub(r'class LoanSummaryViewModel[\s\S]*?\}\n\}', new_impl, content, count=1)

with open(path, "w") as f:
    f.write(content)

