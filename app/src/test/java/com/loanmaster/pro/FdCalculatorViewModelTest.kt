package com.loanmaster.pro

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(org.robolectric.RobolectricTestRunner::class)
class FdCalculatorViewModelTest {

    private lateinit var classUnderTest: FdCalculatorViewModel

    @Before
    fun setup() {
        classUnderTest = FdCalculatorViewModel()
    }

    @Test
    fun `valid inputs calculate maturity`() = runBlocking {
        classUnderTest.updateInputs(
            depositAmount = "100000",
            interestRatePa = "7.5",
            tenureYears = "5"
        )
        
        val state = classUnderTest.uiState.first { it.hasValidInput }
        assertEquals(100000.0, state.totalInvested, 0.01)
        assertTrue(state.maturityValue > 100000.0)
        assertTrue(state.totalReturns > 0.0)
    }

    @Test
    fun `invalid inputs show validation errors`() = runBlocking {
        classUnderTest.updateInputs(
            depositAmount = "0",
            interestRatePa = "7.5",
            tenureYears = "5"
        )
        
        val stateZero = classUnderTest.uiState.first { it.depositText == "0" }
        assertFalse(stateZero.hasValidInput)
        assertEquals("Deposit must be > 0", stateZero.validationError)

        classUnderTest.updateInputs(
            depositAmount = "100000",
            interestRatePa = "7.5",
            tenureYears = "105"
        )
        
        val stateLarge = classUnderTest.uiState.first { it.tenureText == "105" }
        assertFalse(stateLarge.hasValidInput)
        assertEquals("Tenure cannot exceed 100 years", stateLarge.validationError)
    }

    @Test
    fun `compounding frequency affects maturity`() = runBlocking {
        classUnderTest.updateInputs(
            depositAmount = "100000",
            interestRatePa = "7.5",
            tenureYears = "5",
            compoundingFreq = "Monthly"
        )
        val stateMonthly = classUnderTest.uiState.first { it.compounding == CompoundingFrequency.MONTHLY }
        
        classUnderTest.updateInputs(
            compoundingFreq = "Yearly"
        )
        val stateYearly = classUnderTest.uiState.first { it.compounding == CompoundingFrequency.YEARLY }
        
        assertTrue(stateMonthly.maturityValue > stateYearly.maturityValue)
    }

    @Test
    fun `breakdown list size is correct`() = runBlocking {
        classUnderTest.updateInputs(
            depositAmount = "100000",
            interestRatePa = "7.5",
            tenureYears = "3"
        )
        val state = classUnderTest.uiState.first { it.hasValidInput }
        assertTrue(state.breakdown.isNotEmpty())
    }

    @Test
    fun `initializeFromHistory restores values`() = runBlocking {
        val history = CalculationHistory(
            id = 7,
            calculatorType = "FD",
            title = "Test FD",
            param1 = "50000",
            param2 = "6.5",
            param3 = "2",
            param4 = "Monthly"
        )
        
        classUnderTest.initializeFromHistory(history)
        
        val state = classUnderTest.uiState.first { it.depositText == "50000" }
        assertEquals(7, state.currentHistoryId)
        assertEquals("6.5", state.rateText)
        assertEquals("2", state.tenureText)
        assertEquals(CompoundingFrequency.MONTHLY, state.compounding)
    }
}
