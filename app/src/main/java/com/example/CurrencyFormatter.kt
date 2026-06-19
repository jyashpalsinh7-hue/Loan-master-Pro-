package com.example

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {

    fun formatMoney(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
        format.maximumFractionDigits = 0
        return format.format(amount)
    }

    fun formatNumber(amount: Double): String {
        val format = NumberFormat.getNumberInstance(Locale.getDefault())
        format.maximumFractionDigits = 0
        return format.format(amount)
    }
    
    val currentSymbol: String
        get() = NumberFormat.getCurrencyInstance(Locale.getDefault()).currency?.symbol ?: "$"
}
