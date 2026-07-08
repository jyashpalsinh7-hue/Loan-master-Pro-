package com.loanmaster.pro.core.formatter

import java.text.NumberFormat
import java.util.Locale

object CurrencyHelper {
    var currencySymbol: String = "₹"
}

private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
    maximumFractionDigits = 0
}
        
private val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
    maximumFractionDigits = 0
}

fun formatMoney(amount: Double, symbol: String = com.loanmaster.pro.core.formatter.CurrencyHelper.currencySymbol): String {
    
    var result = if (amount <= 0.0) "${symbol}0" else currencyFormat.format(amount)
    
    if (amount > 0.0) {
        val cleanNumber = result.replace(Regex("[^0-9.,]"), "")
        result = "$symbol$cleanNumber"
    }
    
    while (result.contains("$symbol$symbol")) {
        result = result.replace("$symbol$symbol", symbol)
    }
    return result
}

fun formatNumber(amount: Double): String {
    return numberFormat.format(amount)
}
        
val currentCurrencySymbol: String
    get() = CurrencyHelper.currencySymbol
