package com.example

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
        maximumFractionDigits = 0
    }
    
    private val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
        maximumFractionDigits = 0
    }

    fun formatMoney(amount: Double): String {
        return currencyFormat.format(amount)
    }

    fun formatNumber(amount: Double): String {
        return numberFormat.format(amount)
    }
    
    val currentSymbol: String
        get() = currencyFormat.currency?.symbol ?: "$"
}
