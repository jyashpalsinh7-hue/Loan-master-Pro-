import sys

def add_brace_at(filepath, search_str, replacement_str):
    with open(filepath, 'r') as f:
        content = f.read()
    if search_str in content:
        content = content.replace(search_str, replacement_str)
        with open(filepath, 'w') as f:
            f.write(content)
        print(f"Fixed {filepath}")
    else:
        print(f"Failed to fix {filepath}")

add_brace_at(
    'app/src/main/java/com/loanmaster/pro/feature/loansummary/LoanSummaryScreen.kt',
    """        }
    } // closes Box

    if (showAddLoanDialog) {""",
    """        }
        } // closes ResponsiveScreenWrapper
    } // closes Box

    if (showAddLoanDialog) {"""
)
