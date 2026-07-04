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
class RdCalculatorViewModelTest {

    private lateinit var classUnderTest: RdCalculatorViewModel

    @Before
    fun setup() {
        classUnderTest = RdCalculatorViewModel()
    }

    @Test
    fun `standard RD computes correct maturity`() = runBlocking {
        classUnderTest.onEvent(RdEvent.TabChanged("Standard"))
        classUnderTest.onEvent(RdEvent.MonthlyDepositChanged("1000"))
        classUnderTest.onEvent(RdEvent.InterestRateChanged("7.0"))
        classUnderTest.onEvent(RdEvent.TenureChanged("1"))
        
        val state = classUnderTest.uiState.first { it.hasValidInput }
        assertEquals(12000.0, state.totalInvested, 0.01)
        assertTrue(state.maturityValue > 12000.0)
        assertTrue(state.totalReturns > 0.0)
    }

    @Test
    fun `goal based RD computes required deposit`() = runBlocking {
        classUnderTest.onEvent(RdEvent.TabChanged("Goal Based"))
        classUnderTest.onEvent(RdEvent.TargetAmountChanged("20000"))
        classUnderTest.onEvent(RdEvent.InterestRateChanged("7.0"))
        classUnderTest.onEvent(RdEvent.TenureChanged("1"))
        
        val state = classUnderTest.uiState.first { it.hasValidInput }
        assertTrue(state.calculatedMonthlyDeposit > 0)
        assertEquals(20000.0, state.maturityValue, 0.01)
    }

    @Test
    fun `compounding frequency affects maturity value`() = runBlocking {
        classUnderTest.onEvent(RdEvent.TabChanged("Standard"))
        classUnderTest.onEvent(RdEvent.MonthlyDepositChanged("1000"))
        classUnderTest.onEvent(RdEvent.InterestRateChanged("7.0"))
        classUnderTest.onEvent(RdEvent.TenureChanged("5"))
        
        classUnderTest.onEvent(RdEvent.CompoundingFrequencyChanged("Monthly"))
        val stateMonthly = classUnderTest.uiState.first { it.compoundingFrequency == "Monthly" }
        
        classUnderTest.onEvent(RdEvent.CompoundingFrequencyChanged("Yearly"))
        val stateYearly = classUnderTest.uiState.first { it.compoundingFrequency == "Yearly" }
        
        assertTrue(stateMonthly.maturityValue > stateYearly.maturityValue)
    }

    @Test
    fun `invalid inputs return zero values`() = runBlocking {
        classUnderTest.onEvent(RdEvent.TabChanged("Standard"))
        classUnderTest.onEvent(RdEvent.MonthlyDepositChanged("0"))
        classUnderTest.onEvent(RdEvent.InterestRateChanged("7.0"))
        classUnderTest.onEvent(RdEvent.TenureChanged("1"))
        
        val state = classUnderTest.uiState.first()
        assertFalse(state.hasValidInput)
        assertEquals(0.0, state.totalInvested, 0.01)
    }

    @Test
    fun `history initialization sets state`() = runBlocking {
        val history = CalculationHistory(
            id = 5,
            calculatorType = "RD",
            title = "Test",
            param1 = "5000",
            param2 = "8.0",
            param3 = "2",
            param4 = "Monthly",
            param5 = "Standard"
        )
        classUnderTest.onEvent(RdEvent.InitializeFromHistory(history))
        
        val state = classUnderTest.uiState.first { it.monthlyDepositText == "5000" }
        assertEquals("Standard", state.selectedTab)
        assertEquals("5000", state.monthlyDepositText)
        assertEquals("8.0", state.interestRatePaText)
        assertEquals("2", state.tenureYearsText)
        assertEquals("Monthly", state.compoundingFrequency)
    }
}
