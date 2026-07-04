package com.loanmaster.pro

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(org.robolectric.RobolectricTestRunner::class)
class PrepaymentCalculatorViewModelTest {

    private lateinit var classUnderTest: PrepaymentCalculatorViewModel

    @Before
    fun setup() {
        classUnderTest = PrepaymentCalculatorViewModel()
    }

    @Test
    fun `tenure reduction strategy calculates correctly`() = runBlocking {
        classUnderTest.onEvent(PrepaymentEvent.LoanAmountChanged("100000"))
        classUnderTest.onEvent(PrepaymentEvent.InterestRateChanged("10"))
        classUnderTest.onEvent(PrepaymentEvent.TenureChanged("10"))
        classUnderTest.onEvent(PrepaymentEvent.PrepaymentAmountChanged("20000"))
        classUnderTest.onEvent(PrepaymentEvent.StrategyChanged("Tenure"))
        
        val state = classUnderTest.uiState.first { it.hasValidInput }
        assertEquals(1321.51, state.originalEmi, 0.5)
        assertEquals(1321.51, state.newEmi, 0.5) // EMI should remain same
        assertTrue(state.interestSaved > 0.0)
        assertTrue(state.tenureReducedMonths > 0.0)
    }

    @Test
    fun `emi reduction strategy calculates correctly`() = runBlocking {
        classUnderTest.onEvent(PrepaymentEvent.LoanAmountChanged("100000"))
        classUnderTest.onEvent(PrepaymentEvent.InterestRateChanged("10"))
        classUnderTest.onEvent(PrepaymentEvent.TenureChanged("10"))
        classUnderTest.onEvent(PrepaymentEvent.PrepaymentAmountChanged("20000"))
        classUnderTest.onEvent(PrepaymentEvent.StrategyChanged("EMI"))
        
        val state = classUnderTest.uiState.first { it.hasValidInput }
        assertEquals(1321.51, state.originalEmi, 0.5)
        assertTrue(state.newEmi < state.originalEmi)
        assertEquals(0.0, state.tenureReducedMonths, 0.01) // Tenure remains same
    }

    @Test
    fun `monthly and annual extra payments calculate correctly`() = runBlocking {
        classUnderTest.onEvent(PrepaymentEvent.LoanAmountChanged("100000"))
        classUnderTest.onEvent(PrepaymentEvent.InterestRateChanged("10"))
        classUnderTest.onEvent(PrepaymentEvent.TenureChanged("10"))
        classUnderTest.onEvent(PrepaymentEvent.MonthlyPrepaymentChanged("500"))
        classUnderTest.onEvent(PrepaymentEvent.AnnualPrepaymentChanged("1000"))
        classUnderTest.onEvent(PrepaymentEvent.StrategyChanged("Tenure"))
        
        val state = classUnderTest.uiState.first { it.hasValidInput }
        assertTrue(state.interestSaved > 0.0)
        assertTrue(state.tenureReducedMonths > 0.0)
    }

    @Test
    fun `invalid inputs return zero or invalid state`() = runBlocking {
        classUnderTest.onEvent(PrepaymentEvent.LoanAmountChanged("0"))
        classUnderTest.onEvent(PrepaymentEvent.InterestRateChanged("10"))
        classUnderTest.onEvent(PrepaymentEvent.TenureChanged("10"))
        
        val state = classUnderTest.uiState.first { it.loanAmountText == "0" }
        assertFalse(state.hasValidInput)
        assertEquals(0.0, state.originalEmi, 0.01)
    }

    @Test
    fun `initializeFromHistory restores values`() = runBlocking {
        val history = CalculationHistory(
            id = 1,
            calculatorType = "Prepayment",
            title = "Test",
            param1 = "50000",
            param2 = "8.0",
            param3 = "5",
            param4 = "10000",
            param5 = "EMI"
        )
        classUnderTest.onEvent(PrepaymentEvent.InitializeFromHistory(history))
        
        val state = classUnderTest.uiState.first { it.loanAmountText == "50000" }
        assertEquals("50000", state.loanAmountText)
        assertEquals("8.0", state.interestRateText)
        assertEquals("5", state.tenureYearsText)
        assertEquals("10000", state.prepaymentAmountText)
        assertEquals("EMI", state.strategy)
        assertEquals(1, state.currentHistoryId)
    }
}
