package com.loanmaster.pro
import org.junit.Test
import com.loanmaster.pro.core.formatter.formatMoney
class CurrencyTest {
    @Test
    fun testCurrency() {
        println("TEST_CURRENCY 0.0 -> ${formatMoney(0.0)}")
        println("TEST_CURRENCY NaN -> ${formatMoney(Double.NaN)}")
    }
}
