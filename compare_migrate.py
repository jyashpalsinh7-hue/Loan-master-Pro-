import re
import os

vm_path = "app/src/main/java/com/loanmaster/pro/feature/compare/CompareViewModel.kt"
screen_path = "app/src/main/java/com/loanmaster/pro/feature/compare/CompareScreen.kt"

if os.path.exists(vm_path):
    with open(vm_path, "r") as f:
        content = f.read()
    
    content = re.sub(r'sealed class CompareEvent \{.*?\}\n', '', content, flags=re.DOTALL)
    
    new_func = """    fun updateInputs(
        loan1Amount: String? = null,
        loan1Rate: String? = null,
        loan1Tenure: String? = null,
        loan2Amount: String? = null,
        loan2Rate: String? = null,
        loan2Tenure: String? = null
    ) {
        updateState { current ->
            current.copy(
                loan1Amount = loan1Amount ?: current.loan1Amount,
                loan1Rate = loan1Rate ?: current.loan1Rate,
                loan1Tenure = loan1Tenure ?: current.loan1Tenure,
                loan2Amount = loan2Amount ?: current.loan2Amount,
                loan2Rate = loan2Rate ?: current.loan2Rate,
                loan2Tenure = loan2Tenure ?: current.loan2Tenure
            )
        }
    }"""
    
    content = re.sub(r'fun onEvent\(event: CompareEvent\) \{.*?\}\n\n', new_func + "\n\n", content, flags=re.DOTALL)
    with open(vm_path, "w") as f:
        f.write(content)

if os.path.exists(screen_path):
    with open(screen_path, "r") as f:
        content = f.read()
    
    content = re.sub(r'viewModel\.onEvent\(CompareEvent\.Loan1AmountChanged\((.*?)\)\)', r'viewModel.updateInputs(loan1Amount = \1)', content)
    content = re.sub(r'viewModel\.onEvent\(CompareEvent\.Loan1RateChanged\((.*?)\)\)', r'viewModel.updateInputs(loan1Rate = \1)', content)
    content = re.sub(r'viewModel\.onEvent\(CompareEvent\.Loan1TenureChanged\((.*?)\)\)', r'viewModel.updateInputs(loan1Tenure = \1)', content)
    content = re.sub(r'viewModel\.onEvent\(CompareEvent\.Loan2AmountChanged\((.*?)\)\)', r'viewModel.updateInputs(loan2Amount = \1)', content)
    content = re.sub(r'viewModel\.onEvent\(CompareEvent\.Loan2RateChanged\((.*?)\)\)', r'viewModel.updateInputs(loan2Rate = \1)', content)
    content = re.sub(r'viewModel\.onEvent\(CompareEvent\.Loan2TenureChanged\((.*?)\)\)', r'viewModel.updateInputs(loan2Tenure = \1)', content)
    
    with open(screen_path, "w") as f:
        f.write(content)
