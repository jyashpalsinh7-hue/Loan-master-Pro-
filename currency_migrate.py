import re
import os

vm_path = "app/src/main/java/com/loanmaster/pro/feature/currency/CurrencyViewModel.kt"
screen_path = "app/src/main/java/com/loanmaster/pro/feature/currency/CurrencyScreen.kt"

if os.path.exists(vm_path):
    with open(vm_path, "r") as f:
        content = f.read()
    
    content = re.sub(r'sealed class CurrencyEvent \{.*?\}\n', '', content, flags=re.DOTALL)
    
    new_func = """    fun updateInputs(
        baseAmount: String? = null,
        baseCurrency: String? = null,
        targetCurrency: String? = null,
        swapCurrencies: Boolean = false,
        showBaseSelector: Boolean? = null,
        showTargetSelector: Boolean? = null,
        searchQuery: String? = null,
        tabSelected: Int? = null,
        refreshRates: Boolean = false
    ) {
        if (refreshRates) {
            fetchRates()
            return
        }
        
        _uiState.update { current ->
            var next = current
            if (baseAmount != null) next = next.copy(baseAmount = baseAmount)
            if (baseCurrency != null) next = next.copy(baseCurrency = baseCurrency, showBaseSelector = false)
            if (targetCurrency != null) next = next.copy(targetCurrency = targetCurrency, showTargetSelector = false)
            if (showBaseSelector != null) next = next.copy(showBaseSelector = showBaseSelector, searchQuery = "")
            if (showTargetSelector != null) next = next.copy(showTargetSelector = showTargetSelector, searchQuery = "")
            if (searchQuery != null) next = next.copy(searchQuery = searchQuery)
            if (tabSelected != null) next = next.copy(selectedTab = tabSelected)
            
            if (swapCurrencies) {
                val temp = next.baseCurrency
                next = next.copy(baseCurrency = next.targetCurrency, targetCurrency = temp)
            }
            
            // Recalculate
            val baseVal = next.baseAmount.toDoubleOrNull() ?: 0.0
            val targetRate = next.exchangeRates[next.targetCurrency] ?: 0.0
            val baseRate = next.exchangeRates[next.baseCurrency] ?: 1.0
            
            // Rates in API are based on USD by default.
            // value in USD = baseVal / baseRate
            // targetValue = (baseVal / baseRate) * targetRate
            
            val targetVal = if (baseRate > 0) (baseVal / baseRate) * targetRate else 0.0
            next.copy(targetAmount = targetVal)
        }
    }"""
    
    content = re.sub(r'fun onEvent\(event: CurrencyEvent\) \{.*?\}\n\n', new_func + "\n\n", content, flags=re.DOTALL)
    with open(vm_path, "w") as f:
        f.write(content)

if os.path.exists(screen_path):
    with open(screen_path, "r") as f:
        content = f.read()
    
    content = content.replace("viewModel.onEvent(CurrencyEvent.RefreshRates)", "viewModel.updateInputs(refreshRates = true)")
    content = content.replace("viewModel.onEvent(CurrencyEvent.BaseAmountChanged(it))", "viewModel.updateInputs(baseAmount = it)")
    content = content.replace("viewModel.onEvent(CurrencyEvent.SwapCurrencies)", "viewModel.updateInputs(swapCurrencies = true)")
    content = content.replace("viewModel.onEvent(CurrencyEvent.ShowBaseSelector(true))", "viewModel.updateInputs(showBaseSelector = true)")
    content = content.replace("viewModel.onEvent(CurrencyEvent.ShowTargetSelector(true))", "viewModel.updateInputs(showTargetSelector = true)")
    content = content.replace("viewModel.onEvent(CurrencyEvent.ShowBaseSelector(false))", "viewModel.updateInputs(showBaseSelector = false)")
    content = content.replace("viewModel.onEvent(CurrencyEvent.ShowTargetSelector(false))", "viewModel.updateInputs(showTargetSelector = false)")
    content = content.replace("viewModel.onEvent(CurrencyEvent.SearchQueryChanged(it))", "viewModel.updateInputs(searchQuery = it)")
    content = re.sub(r'viewModel\.onEvent\(CurrencyEvent\.BaseCurrencySelected\((.*?)\)\)', r'viewModel.updateInputs(baseCurrency = \1)', content)
    content = re.sub(r'viewModel\.onEvent\(CurrencyEvent\.TargetCurrencySelected\((.*?)\)\)', r'viewModel.updateInputs(targetCurrency = \1)', content)
    content = re.sub(r'viewModel\.onEvent\(CurrencyEvent\.TabSelected\((.*?)\)\)', r'viewModel.updateInputs(tabSelected = \1)', content)
    
    with open(screen_path, "w") as f:
        f.write(content)

