package com.loanmaster.pro.core.formatter

import java.text.NumberFormat
import java.util.Locale

private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
    maximumFractionDigits = 0
}
        
private val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
    maximumFractionDigits = 0
}

fun formatMoney(amount: Double): String {
    if (amount <= 0.0) return "₹0"
    return currencyFormat.format(amount)
}

fun formatNumber(amount: Double): String {
    return numberFormat.format(amount)
}
        
val currentCurrencySymbol: String
    get() = currencyFormat.currency?.symbol ?: "₹"
