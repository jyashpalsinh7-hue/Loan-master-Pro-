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
class SipCalculatorViewModelTest {

    private lateinit var classUnderTest: SipCalculatorViewModel

    @Before
    fun setup() {
        classUnderTest = SipCalculatorViewModel()
    }

    @Test
    fun `valid inputs compute correct maturity value`() = runBlocking {
        classUnderTest.onEvent(SipEvent.AmountChanged("1000"))
        classUnderTest.onEvent(SipEvent.ReturnRateChanged("12"))
        classUnderTest.onEvent(SipEvent.YearsChanged("1"))
        
        val state = classUnderTest.uiState.first { it.hasValidInput }
        assertEquals(12000.0, state.totalInvested, 0.1)
        // FV = P * [(1+i)^n - 1]/i * (1+i) where P=1000, n=12, i=0.01 -> ~ 12682.50
        assertEquals(12809.33, state.maturityValue, 1.0)
        assertTrue(state.hasValidInput)
    }

    @Test
    fun `invalid inputs return zero maturity value`() = runBlocking {
        classUnderTest.onEvent(SipEvent.AmountChanged("0"))
        classUnderTest.onEvent(SipEvent.ReturnRateChanged("12"))
        classUnderTest.onEvent(SipEvent.YearsChanged("1"))
        
        val state = classUnderTest.uiState.first()
        assertEquals(0.0, state.totalInvested, 0.1)
        assertEquals(0.0, state.maturityValue, 0.1)
        assertFalse(state.hasValidInput)
    }

    @Test
    fun `step up updates total invested correctly`() = runBlocking {
        classUnderTest.onEvent(SipEvent.AmountChanged("1000"))
        classUnderTest.onEvent(SipEvent.ReturnRateChanged("10"))
        classUnderTest.onEvent(SipEvent.YearsChanged("2"))
        classUnderTest.onEvent(SipEvent.StepUpChanged("10"))
        
        val state = classUnderTest.uiState.first { it.hasValidInput }
        // Year 1: 12000. Year 2: 13200. Total = 25200.
        assertEquals(25200.0, state.totalInvested, 0.1)
    }

    @Test
    fun `yearly data list size matches years input`() = runBlocking {
        classUnderTest.onEvent(SipEvent.AmountChanged("1000"))
        classUnderTest.onEvent(SipEvent.ReturnRateChanged("10"))
        classUnderTest.onEvent(SipEvent.YearsChanged("5"))
        
        val state = classUnderTest.uiState.first { it.hasValidInput }
        assertEquals(5, state.yearlyDataList.size)
    }

    @Test
    fun `initializeFromHistory populates ui state correctly`() = runBlocking {
        val history = CalculationHistory(
            id = 10,
            calculatorType = "SIP",
            title = "Test SIP",
            param1 = "2000",
            param2 = "12",
            param3 = "3",
            param4 = "5",
            timestamp = 1000L
        )
        classUnderTest.onEvent(SipEvent.InitializeFromHistory(history))
        
        val state = classUnderTest.uiState.first { it.hasValidInput }
        assertEquals("2000", state.amountText)
        assertEquals("12", state.returnRateText)
        assertEquals("3", state.yearsText)
        assertEquals("5", state.stepUpText)
        assertEquals(10, state.currentHistoryId)
        assertTrue(state.hasValidInput)
    }
}
