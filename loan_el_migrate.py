import re
import os

vm_path = "app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityViewModel.kt"
screen_path = "app/src/main/java/com/loanmaster/pro/feature/loaneligibility/LoanEligibilityScreen.kt"

if os.path.exists(vm_path):
    with open(vm_path, "r") as f:
        content = f.read()
    
    content = re.sub(r'sealed class LoanEligibilityEvent \{.*?\}\n', '', content, flags=re.DOTALL)
    
    new_func = """    fun updateInputs(
        profile: String? = null,
        defaultTenure: String? = null,
        defaultRate: String? = null,
        income: String? = null,
        emi: String? = null,
        isCoBorrowerEnabled: Boolean? = null,
        coIncome: String? = null,
        coEmi: String? = null,
        tenure: String? = null,
        rate: String? = null,
        isSalaried: Boolean? = null,
        creditScoreRange: String? = null,
        adjustIncomeAmount: Double? = null,
        adjustEmiAmount: Double? = null,
        adjustTenureYears: Double? = null
    ) {
        updateState { current ->
            var next = current
            if (profile != null && defaultTenure != null && defaultRate != null) {
                next = next.copy(selectedLoanProfile = profile, tenureYearsText = defaultTenure, interestRateText = defaultRate)
            }
            if (income != null) next = next.copy(monthlyIncomeText = income)
            if (emi != null) next = next.copy(existingEMIsText = emi)
            if (isCoBorrowerEnabled != null) next = next.copy(isCoBorrowerEnabled = isCoBorrowerEnabled)
            if (coIncome != null) next = next.copy(coBorrowerIncomeText = coIncome)
            if (coEmi != null) next = next.copy(coBorrowerEMIsText = coEmi)
            if (tenure != null) next = next.copy(tenureYearsText = tenure)
            if (rate != null) next = next.copy(interestRateText = rate)
            if (isSalaried != null) next = next.copy(isSalaried = isSalaried)
            if (creditScoreRange != null) next = next.copy(creditScoreRange = creditScoreRange)
            
            if (adjustIncomeAmount != null) {
                next = next.copy(monthlyIncomeText = ((next.monthlyIncomeText.toDoubleOrNull() ?: 0.0) + adjustIncomeAmount).coerceAtLeast(0.0).toString())
            }
            if (adjustEmiAmount != null) {
                next = next.copy(existingEMIsText = ((next.existingEMIsText.toDoubleOrNull() ?: 0.0) + adjustEmiAmount).coerceAtLeast(0.0).toString())
            }
            if (adjustTenureYears != null) {
                next = next.copy(tenureYearsText = ((next.tenureYearsText.toDoubleOrNull() ?: 0.0) + adjustTenureYears).coerceAtLeast(1.0).toString())
            }
            next
        }
    }"""
    
    content = re.sub(r'fun onEvent\(event: LoanEligibilityEvent\) \{.*?\}\n\n', new_func + "\n\n", content, flags=re.DOTALL)
    
    with open(vm_path, "w") as f:
        f.write(content)

if os.path.exists(screen_path):
    with open(screen_path, "r") as f:
        content = f.read()
    
    content = re.sub(r'viewModel\.onEvent\(LoanEligibilityEvent\.ProfileChanged\((.*?), (.*?), (.*?)\)\)', r'viewModel.updateInputs(profile = \1, defaultTenure = \2, defaultRate = \3)', content)
    content = re.sub(r'viewModel\.onEvent\(LoanEligibilityEvent\.MonthlyIncomeChanged\((.*?)\)\)', r'viewModel.updateInputs(income = \1)', content)
    content = re.sub(r'viewModel\.onEvent\(LoanEligibilityEvent\.ExistingEMIsChanged\((.*?)\)\)', r'viewModel.updateInputs(emi = \1)', content)
    content = re.sub(r'viewModel\.onEvent\(LoanEligibilityEvent\.CoBorrowerToggled\((.*?)\)\)', r'viewModel.updateInputs(isCoBorrowerEnabled = \1)', content)
    content = re.sub(r'viewModel\.onEvent\(LoanEligibilityEvent\.CoBorrowerIncomeChanged\((.*?)\)\)', r'viewModel.updateInputs(coIncome = \1)', content)
    content = re.sub(r'viewModel\.onEvent\(LoanEligibilityEvent\.CoBorrowerEMIsChanged\((.*?)\)\)', r'viewModel.updateInputs(coEmi = \1)', content)
    content = re.sub(r'viewModel\.onEvent\(LoanEligibilityEvent\.TenureChanged\((.*?)\)\)', r'viewModel.updateInputs(tenure = \1)', content)
    content = re.sub(r'viewModel\.onEvent\(LoanEligibilityEvent\.InterestRateChanged\((.*?)\)\)', r'viewModel.updateInputs(rate = \1)', content)
    content = re.sub(r'viewModel\.onEvent\(LoanEligibilityEvent\.EmploymentTypeChanged\((.*?)\)\)', r'viewModel.updateInputs(isSalaried = \1)', content)
    content = re.sub(r'viewModel\.onEvent\(LoanEligibilityEvent\.CreditScoreChanged\((.*?)\)\)', r'viewModel.updateInputs(creditScoreRange = \1)', content)
    
    content = re.sub(r'viewModel\.onEvent\(LoanEligibilityEvent\.AdjustIncome\((.*?)\)\)', r'viewModel.updateInputs(adjustIncomeAmount = \1.toDouble())', content)
    content = re.sub(r'viewModel\.onEvent\(LoanEligibilityEvent\.AdjustEmi\((.*?)\)\)', r'viewModel.updateInputs(adjustEmiAmount = \1.toDouble())', content)
    content = re.sub(r'viewModel\.onEvent\(LoanEligibilityEvent\.AdjustTenure\((.*?)\)\)', r'viewModel.updateInputs(adjustTenureYears = \1.toDouble())', content)
    
    with open(screen_path, "w") as f:
        f.write(content)
