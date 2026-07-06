import os

# fix LoanSummaryViewModel.kt
path = "app/src/main/java/com/loanmaster/pro/feature/loansummary/LoanSummaryViewModel.kt"
with open(path, "r") as f:
    content = f.read()

if "data class LoanSummaryUiState" not in content:
    ui_state_class = """data class LoanSummaryUiState(
    val activeLoans: List<ActiveLoan> = emptyList()
)

"""
    content = content.replace("class LoanSummaryViewModel(", ui_state_class + "class LoanSummaryViewModel(")
    # we need to change how activeLoans is managed
    # Actually wait, activeLoans is from repository.getAllActiveLoans() directly...
    
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
}"""
    content = content.replace("""class LoanSummaryViewModel(private val repository: ActiveLoanRepository) : ViewModel() {

    val activeLoans: StateFlow<List<ActiveLoan>> = repository.getAllActiveLoans()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
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
}""", new_impl)
    content = content.replace("import kotlinx.coroutines.flow.stateIn", "import kotlinx.coroutines.flow.stateIn\nimport kotlinx.coroutines.flow.map")
    
    with open(path, "w") as f:
        f.write(content)

