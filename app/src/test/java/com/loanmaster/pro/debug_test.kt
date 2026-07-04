package com.loanmaster.pro

import org.junit.Test
import org.junit.Assert.*

class DebugTest {
    @Test
    fun debugThis() {
        val repo = HistoryRepository(FakeHistoryDao())
        val classUnderTest = EmiCalculatorViewModel(repo)
        
        val mockHistory = CalculationHistory(
            id = 99,
            calculatorType = "EMI",
            title = "Test",
            param1 = "200000",
            param2 = "8.5",
            param3 = "5",
            param4 = "false",
            param5 = "Car Loan",
            timestamp = 12345L
        )
        classUnderTest.loadFromHistory(mockHistory)
        println("valid: ${classUnderTest.hasValidInput.value}")
        println("loan: ${classUnderTest.loanAmountText.value}")
        println("rate: ${classUnderTest.interestRateText.value}")
        println("tenure: ${classUnderTest.tenureInputText.value}")
        println("months: ${classUnderTest.totalMonths.value}")
    }
}
