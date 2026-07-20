import os

filepath = 'app/src/main/java/com/loanmaster/pro/feature/currency/CurrencyScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

target = """        if (showTargetSelector) {
            CurrencySelectorSheet(
                currencies = distinctCurrencies,
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = { viewModel.updateInputs(searchQuery = it) },
                onDismissRequest = { viewModel.updateInputs(showTargetSelector = false) },
                onCurrencySelected = { code -> viewModel.updateInputs(targetCurrency = code) }
            )
        }
    }
}

        }"""

replacement = """        if (showTargetSelector) {
            CurrencySelectorSheet(
                currencies = distinctCurrencies,
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = { viewModel.updateInputs(searchQuery = it) },
                onDismissRequest = { viewModel.updateInputs(showTargetSelector = false) },
                onCurrencySelected = { code -> viewModel.updateInputs(targetCurrency = code) }
            )
        }
    }
    }
    }
}
"""

if target in content:
    content = content.replace(target, replacement)
    with open(filepath, 'w') as f:
        f.write(content)
    print("Fixed CurrencyScreen.kt closures")
else:
    print("Target not found.")

