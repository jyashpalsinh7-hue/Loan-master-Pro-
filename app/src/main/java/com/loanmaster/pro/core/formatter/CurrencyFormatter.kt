package com.loanmaster.pro.core.formatter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import com.loanmaster.pro.LocalCurrencySymbol
import java.text.NumberFormat
import java.util.Locale

// Keep for backward compatibility (but mark as deprecated)
@Deprecated(
    message = "Use formatMoney() inside Composables or pass symbol explicitly. " +
            "This global var will be removed in future versions.",
    level = DeprecationLevel.WARNING
)
object CurrencyHelper {
    var currencySymbol: String = "₹"
}

private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
    maximumFractionDigits = 0
}

private val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
    maximumFractionDigits = 0
}

/**
 * Format money using the provided symbol.
 * Falls back to the global value if no symbol is provided.
 */
fun formatMoney(
    amount: Double,
    symbol: String? = null
): String {
    val effectiveSymbol = symbol ?: CurrencyHelper.currencySymbol

    var result = if (amount <= 0.0) "${effectiveSymbol}0" else currencyFormat.format(amount)

    if (amount > 0.0) {
        val cleanNumber = result.replace(Regex("[^0-9.,]"), "")
        result = "$effectiveSymbol$cleanNumber"
    }

    while (result.contains("$effectiveSymbol$effectiveSymbol")) {
        result = result.replace("$effectiveSymbol$effectiveSymbol", effectiveSymbol)
    }
    return result
}

fun formatNumber(amount: Double): String {
    return numberFormat.format(amount)
}

val currentCurrencySymbol: String
    @ReadOnlyComposable
    @Composable
    get() = LocalCurrencySymbol.current
