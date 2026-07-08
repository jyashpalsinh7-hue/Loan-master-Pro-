package com.loanmaster.pro.core.formatter

import androidx.compose.runtime.Composable
import com.loanmaster.pro.LocalCurrencySymbol

@Composable
fun formatMoneyComposable(amount: Double): String {
    val symbol = LocalCurrencySymbol.current
    return com.loanmaster.pro.core.formatter.formatMoney(amount, symbol)
}
